package com.truth99.life.rest;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.ws.rs.*;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * Created by LD on 2016/10/17 0017.
 */
@Path("/pic")
public class Picture {

    private static final String UPLOADED_FILE_PATH = "D:\\A\\pic\\";

    @GET
    @Path("/get3")
    @Produces("image/*")
    public File getFile(){
        File samplePDF = new File("D:\\A\\pic\\IMG_0775.PNG");
        return samplePDF;
    }

    @GET
    @Path("/get")
    @Produces("image/*")
    public InputStream test(){
        String pic_path = "D:\\A\\pic\\LD.png";
        System.out.println(pic_path);
        try {
            FileInputStream is = new FileInputStream(pic_path);
            return is;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @GET
    @Path("/get2")
    @Produces("image/*")
    public StreamingOutput getFileStreamingOutput() throws Exception{

        return new StreamingOutput() {
            @Override
            public void write(OutputStream outputStream) throws IOException,
                    WebApplicationException {
                FileInputStream inputStream = new FileInputStream("D:\\A\\pic\\image.jpg");
                int nextByte = 0;
                while((nextByte  = inputStream.read()) != -1 ){
                    outputStream.write(nextByte);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();
            }
        };
    }

    @Path("/upload")
    @POST
    @Consumes("multipart/form-data")
    public Response uploadFile(MultipartFormDataInput input) {
        String fileName = "";
        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        List<InputPart> inputParts = uploadForm.get("userPhoto");
        for (InputPart inputPart : inputParts) {
            try {
                MultivaluedMap<String, String> header = inputPart.getHeaders();
                fileName = getFileName(header);
                //convert the uploaded file to inputstream
                InputStream inputStream = inputPart.getBody(InputStream.class,null);
                byte [] bytes = IOUtils.toByteArray(inputStream);
                //constructs upload file path
                fileName = UPLOADED_FILE_PATH + fileName;
                writeFile(bytes,fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Response.status(200)
                .entity("uploadFile is called, Uploaded file name : " + fileName).build();
    }

    private String getFileName(MultivaluedMap<String, String> header) {
        String[] contentDisposition = header.getFirst("Content-Disposition").split(";");
        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {
                String[] name = filename.split("=");
                String finalFileName = name[1].trim().replaceAll("\"", "");
                return finalFileName;
            }
        }
        return "unknown";
    }

    /**
     * 保存图片
     * @param content
     * @param filename
     * @throws IOException
     */
    private void writeFile(byte[] content, String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fop = new FileOutputStream(file);
        fop.write(content);
        fop.flush();
        fop.close();
    }
}
