package _dduartess.travelaencymanagement.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ContentDisposition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import _dduartess.travelaencymanagement.entities.contract.Contract;
import _dduartess.travelaencymanagement.entities.contract.ContractPassenger;
import _dduartess.travelaencymanagement.entities.trip.RoomType;
import _dduartess.travelaencymanagement.entities.trip.Trip;
import _dduartess.travelaencymanagement.repositories.ContractPassengerRepository;
import _dduartess.travelaencymanagement.repositories.ContractRepository;

@Service
public class ContractPdfService {

    private final ContractRepository contractRepository;
    private final ContractPassengerRepository contractPassengerRepository;

    @Value("${app.contracts.template-path:templates/template-contrato.pdf}")
    private String templatePath;

    @Value("${app.contracts.pdf-directory:./contracts}")
    private String pdfDir;

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ContractPdfService(ContractRepository contractRepository,
                              ContractPassengerRepository contractPassengerRepository) {
        this.contractRepository = contractRepository;
        this.contractPassengerRepository = contractPassengerRepository;
    }

    @Transactional(readOnly = true)
    public PdfResult generateContractPdf(Long contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("Contrato não encontrado com ID: " + contractId));

        Trip trip = contract.getTrip();

        List<ContractPassenger> cps = contractPassengerRepository.findByContractId(contractId).stream()
                .sorted(Comparator.comparing(ContractPassenger::getId))
                .toList();

        byte[] pdfBytes = fillTemplate(contract, trip, cps);

        try {
            Path outDir = Paths.get(pdfDir);
            Files.createDirectories(outDir);
            Path outFile = outDir.resolve("contract-" + contractId + ".pdf");
            Files.write(outFile, pdfBytes);
        } catch (IOException e) {
        }

        String filename = "contract-" + contractId + ".pdf";
        ContentDisposition cd = ContentDisposition.attachment().filename(filename).build();
        return new PdfResult(pdfBytes, cd.toString());
    }

    private byte[] fillTemplate(Contract contract, Trip trip, List<ContractPassenger> cps) {
        try (var in = new ClassPathResource(templatePath).getInputStream()) {

            byte[] templateBytes = in.readAllBytes();

            try (PDDocument doc = Loader.loadPDF(templateBytes);
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {

                PDType1Font helvetica = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

                PDPage page1 = doc.getPage(0);
                try (PDPageContentStream cs = new PDPageContentStream(doc, page1, AppendMode.APPEND, true, true)) {
                    cs.setFont(helvetica, 10);

                    write(cs, 90, 635, name(cps, 0).toUpperCase());
                    write(cs, 315   , 635, phone(cps, 0));

                    write(cs, 260, 590, cpf(cps, 0));
                    write(cs, 435, 590, birth(cps, 0));

                    write(cs, 90, 550, name(cps, 1).toUpperCase());
                    write(cs, 260, 507, cpf(cps, 1));
                    write(cs, 435, 507, birth(cps, 1));

                    write(cs, 90, 465, name(cps, 2).toUpperCase());
                    write(cs, 260, 425, cpf(cps, 2));
                    write(cs, 435, 425, birth(cps, 2));

                    write(cs, 90, 380, name(cps, 3).toUpperCase());
                    write(cs, 260, 338, cpf(cps, 3));
                    write(cs, 435, 338, birth(cps, 3));

                    write(cs, 90, 295, name(cps, 4).toUpperCase());
                    write(cs, 260, 250, cpf(cps, 4));
                    write(cs, 435, 250, birth(cps, 4));

                    write(cs, 110, 230, trip.getStartDate() != null ? trip.getStartDate().format(DF) : "");
                    write(cs, 260, 230, trip.getEndDate()   != null ? trip.getEndDate().format(DF)   : "");
                    write(cs, 390, 230, safe(trip.getDestination()).toUpperCase());

                    BigDecimal total = calculateTotal(trip, cps);
                    write(cs, 165, 178, total.toString());
                }

                if (doc.getNumberOfPages() >= 2) {
                    PDPage page2 = doc.getPage(1);
                    try (PDPageContentStream cs2 = new PDPageContentStream(doc, page2, AppendMode.APPEND, true, true)) {
                        cs2.setFont(helvetica, 10);

                        String contratante = name(cps, 0);
                        write(cs2,  35, 215, contratante.toUpperCase());

                        cs2.setFont(helvetica, 10);
                        String dia = contract.getCreatedAt() != null ? String.valueOf(contract.getCreatedAt().getDayOfMonth()) : "";
                        String mes = contract.getCreatedAt() != null ? String.valueOf(contract.getCreatedAt().getMonthValue()) : "";
                        String ano = contract.getCreatedAt() != null ? String.valueOf(contract.getCreatedAt().getYear()) : "";

                        write(cs2, 160, 175, dia);
                        write(cs2, 210, 175, mes);
                        write(cs2, 260, 175, ano);
                    }
                }

                doc.save(out);
                return out.toByteArray();
            }

        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar PDF pelo template: " + e.getMessage(), e);
        }
    }

    private BigDecimal calculateTotal(Trip trip, List<ContractPassenger> cps) {
        BigDecimal total = BigDecimal.ZERO;

        for (ContractPassenger cp : cps) {
            if (cp == null) continue;

            BigDecimal price = cp.getPriceSnapshot();
            if (price == null) {
                RoomType rt = cp.getRoomType();
                if (rt != null && trip.getRoomPrices() != null) {
                    price = trip.getRoomPrices().get(rt);
                }
            }

            if (price != null) {
                total = total.add(price);
            }
        }
        return total;
    }

    private void write(PDPageContentStream cs, float x, float y, String text) throws IOException {
        if (text == null) text = "";
        cs.beginText();
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
    }

    private String name(List<ContractPassenger> cps, int i) {
        return cps.size() > i && cps.get(i) != null && cps.get(i).getCustomer() != null
                ? safe(cps.get(i).getCustomer().getName())
                : "";
    }

    private String cpf(List<ContractPassenger> cps, int i) {
        return cps.size() > i && cps.get(i) != null && cps.get(i).getCustomer() != null
                ? safe(cps.get(i).getCustomer().getDocumentNumber())
                : "";
    }

    private String birth(List<ContractPassenger> cps, int i) {
        return cps.size() > i && cps.get(i) != null && cps.get(i).getCustomer() != null
                && cps.get(i).getCustomer().getBirthDate() != null
                ? cps.get(i).getCustomer().getBirthDate().format(DF)
                : "";
    }

    private String phone(List<ContractPassenger> cps, int i) {
        return cps.size() > i && cps.get(i) != null && cps.get(i).getCustomer() != null
                ? safe(cps.get(i).getCustomer().getPhoneNumber())
                : "";
    }

    private String safe(String s) {
        if (s == null) return "";
        return s.replace("\n", " ").replace("\r", " ").trim();
    }

    public record PdfResult(byte[] bytes, String contentDisposition) {}
}