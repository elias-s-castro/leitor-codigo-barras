package br.com.castro.leitorcodigobarras.controller;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.DecodeHintType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

@Controller
public class BarcodeController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, Model model) {
        if (file.isEmpty()) {
            model.addAttribute("message", "Por favor, selecione um arquivo para upload.");
            return "index";
        }

        readFile(file, model);

        return "index";
    }

    private void readFile(MultipartFile file, Model model) {
        try {
            String fileName = file.getOriginalFilename();
            if (fileName != null && fileName.toLowerCase().endsWith(".pdf")) {
                // Processar arquivo PDF
                String result = readBarcodesFromPDF(file.getInputStream());
                model.addAttribute("message", result);
            } else {
                // Processar imagem
                String result = readBarcodeFromImage(file.getInputStream());
                model.addAttribute("message", result);
            }
        } catch (IOException e) {
            model.addAttribute("message", "Erro ao processar o arquivo. Por favor, tente novamente.");
        }
    }

    private String readBarcodeFromImage(InputStream inputStream) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(inputStream);
        if (bufferedImage == null) {
            return "Não foi possível ler a imagem.";
        }

        return decodeBarcode(bufferedImage);
    }

    private String readBarcodesFromPDF(InputStream inputStream) throws IOException {
        PDDocument document = PDDocument.load(inputStream);
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        StringBuilder result = new StringBuilder();

        for (int page = 0; page < document.getNumberOfPages(); ++page) {
            BufferedImage image = pdfRenderer.renderImageWithDPI(page, 600);
            String pageResult = decodeBarcode(image);
            if (!pageResult.contains("Nenhum código de barras encontrado")) {
                result.append("Página ").append(page + 1).append(": ").append(pageResult).append("\n");
            }
        }

        document.close();
        return result.length() > 0 ? result.toString() : "Nenhum código de barras encontrado no PDF.";
    }

    private String decodeBarcode(BufferedImage image) {
        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, Arrays.asList(
                BarcodeFormat.AZTEC,
                BarcodeFormat.CODABAR,
                BarcodeFormat.CODE_39,
                BarcodeFormat.CODE_93,
                BarcodeFormat.CODE_128,
                BarcodeFormat.DATA_MATRIX,
                BarcodeFormat.EAN_8,
                BarcodeFormat.EAN_13,
                BarcodeFormat.ITF,
                BarcodeFormat.PDF_417,
//                BarcodeFormat.QR_CODE,
                BarcodeFormat.RSS_14,
                BarcodeFormat.RSS_EXPANDED,
                BarcodeFormat.UPC_A,
                BarcodeFormat.UPC_E,
                BarcodeFormat.UPC_EAN_EXTENSION
        ));

        try {
            Reader reader = new MultiFormatReader();
            Result result = reader.decode(bitmap, hints);
            return "Código de barras encontrado: " + result.getText();
        } catch (NotFoundException | ChecksumException | FormatException e) {
            return "Nenhum código de barras encontrado.";
        }
    }
}
