package com.api.templateconverter.service;



import com.api.templateconverter.dto.DadosArquivo;
import com.api.templateconverter.util.CompactacaoUtil;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.utils.PdfMerger;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;


@Service
public class PdfConverterService {

        public String convertPdf(DadosArquivo obj) throws IOException {
            return this.convertHtmlPdf(obj);
        }

    public String convertHtmlPdf(DadosArquivo obj) throws IOException {
        String comprovanteHtml = "";
        String arquivoPDFString = "";


        comprovanteHtml =  parseThymeleafTemplate(obj);

        ByteArrayOutputStream tempPdf = createPdf(comprovanteHtml);
        ByteArrayOutputStream arquivoPDF = new ByteArrayOutputStream();
        scalePdf(arquivoPDF, new ByteArrayInputStream(tempPdf.toByteArray()), 0.7071f);
        arquivoPDFString = CompactacaoUtil.compactarTransformarBase64(arquivoPDF.toByteArray());
        return arquivoPDFString;
    }

    private ByteArrayOutputStream createPdf(String htmlSrc) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ConverterProperties converterProperties = new ConverterProperties();

        //"converterProperties.setBaseUri(this.getClass().getClassLoader().getResource("fonts").toURI().toString() + "/nome_fonte");"

        PdfWriter writer = new PdfWriter(output);
        PdfDocument pdfDocument = new PdfDocument(writer);
        PdfMerger merger = new PdfMerger(pdfDocument);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument temp = new PdfDocument(new PdfWriter(baos));
        temp.setDefaultPageSize(PageSize.A3);
        HtmlConverter.convertToPdf(htmlSrc, temp, converterProperties);
        temp = new PdfDocument(new PdfReader(new ByteArrayInputStream(baos.toByteArray())));
        merger.merge(temp, 1, temp.getNumberOfPages());
        temp.close();
        pdfDocument.close();

        return output;
    }
    public void scalePdf(ByteArrayOutputStream output, ByteArrayInputStream input, float scale) throws IOException {
        // Create the source document
        PdfDocument srcDoc = new PdfDocument(new PdfReader(input));
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(output));
        ScaleDownEventHandler eventHandler = new ScaleDownEventHandler(scale);
        int n = srcDoc.getNumberOfPages();
        pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, eventHandler);

        PdfCanvas canvas;
        PdfFormXObject page;
        for (int p = 1; p <= n; p++) {
            eventHandler.setPageDict(srcDoc.getPage(p).getPdfObject());
            canvas = new PdfCanvas(pdfDoc.addNewPage());
            page = srcDoc.getPage(p).copyAsFormXObject(pdfDoc);
            canvas.addXObject(page, scale, 0f, 0f, scale, 0f, 0f);
        }

        pdfDoc.close();
        srcDoc.close();
    }

    protected class ScaleDownEventHandler implements IEventHandler {
        protected float scale = 1;
        protected PdfDictionary pageDict;

        public ScaleDownEventHandler(float scale) {
            this.scale = scale;
        }

        public void setPageDict(PdfDictionary pageDict) {
            this.pageDict = pageDict;
        }

        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfPage page = docEvent.getPage();
            if (pageDict.getAsNumber(PdfName.Rotate) != null)
                page.put(PdfName.Rotate, pageDict.getAsNumber(PdfName.Rotate));

            scaleDown(page, pageDict, PdfName.MediaBox, scale);
            scaleDown(page, pageDict, PdfName.CropBox, scale);
        }

        protected void scaleDown(PdfPage destPage, PdfDictionary pageDictSrc, PdfName box, float scale) {
            PdfArray original = pageDictSrc.getAsArray(box);
            if (original != null) {
                float width = original.getAsNumber(2).floatValue() - original.getAsNumber(0).floatValue();
                float height = original.getAsNumber(3).floatValue() - original.getAsNumber(1).floatValue();
                PdfArray result = new PdfArray();
                result.add(new PdfNumber(0));
                result.add(new PdfNumber(0));
                result.add(new PdfNumber(width * scale));
                result.add(new PdfNumber(height * scale));
                destPage.put(box, result);
            }
        }
    }

    private String parseThymeleafTemplate(DadosArquivo dadosArquivo) {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        Context context = new Context();


        Field[] fields = DadosArquivo.class.getDeclaredFields();

        for (Field field : fields) {
            try {
                // Make the field accessible (even if it's private)
                field.setAccessible(true);

                // Set Thymeleaf variable using field name and field value
                context.setVariable(field.getName(), field.get(dadosArquivo));

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }


        return templateEngine.process("thymeleaf_template", context);
    }







//Implementação com Lib: flying-saucer-pdf
//    public void convertPdf() throws DocumentException, IOException {
//        String html = parseThymeleafTemplate();
//        generatePdfFromHtml(html);
//    }
//
//    private String parseThymeleafTemplate() {
//        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
//        templateResolver.setSuffix(".html");
//        templateResolver.setTemplateMode(TemplateMode.HTML);
//
//        TemplateEngine templateEngine = new TemplateEngine();
//        templateEngine.setTemplateResolver(templateResolver);
//
//        Context context = new Context();
//        context.setVariable("to", "Testannnnnnnnnnnnnnndo");
//
//        return templateEngine.process("thymeleaf_template", context);
//    }
//
//    public void generatePdfFromHtml(String html) throws IOException, DocumentException {
//        String outputFolder = System.getProperty("user.home") + File.separator + "thymeleaf.pdf";
//        OutputStream outputStream = new FileOutputStream(outputFolder);
//
//        ITextRenderer renderer = new ITextRenderer();
//        renderer.setDocumentFromString(html);
//        renderer.layout();
//        renderer.createPDF(outputStream);
//
//        outputStream.close();
//    }
}
