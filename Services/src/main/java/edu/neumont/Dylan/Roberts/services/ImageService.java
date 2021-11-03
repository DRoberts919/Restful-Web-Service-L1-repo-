package edu.neumont.Dylan.Roberts.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class ImageService {
    public BufferedImage getBufferedImage(String name) throws IOException {

        BufferedImage bImage;
        try {
            bImage = ImageIO.read(new File("images/" + name));
            return bImage;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public String createImage(String directory, String imageName, MultipartFile multipartFile) throws IOException {

        Path uploadPath = Paths.get(directory);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        try (InputStream inputStream = multipartFile.getInputStream()) {

            Path filesPath = uploadPath.resolve(imageName);
            Files.copy(inputStream, filesPath, StandardCopyOption.REPLACE_EXISTING);

            return imageName;

        } catch (IOException ioe) {
            throw new IOException("Image cant be saved" + imageName, ioe);
        }


    }


    public byte[] getImage(String name) throws IOException {
        BufferedImage image = getBufferedImage(name);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", bos);
        byte[] data = bos.toByteArray();


        return data;
    }




    public byte[] grayScaleImage(String name) throws IOException {
        BufferedImage image = getBufferedImage(name);
        int width = image.getWidth();
        int height = image.getHeight();

        for(int i=0; i<height; i++) {

            for(int j=0; j<width; j++) {

                Color c = new Color(image.getRGB(j, i));
                int red = (int)(c.getRed() * 0.299);
                int green = (int)(c.getGreen() * 0.587);
                int blue = (int)(c.getBlue() *0.114);
                Color newColor = new Color(red+green+blue,

                        red+green+blue,red+green+blue);

                image.setRGB(j,i,newColor.getRGB());
            }
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", bos);
        byte[] data = bos.toByteArray();

        return data;

    }

    public byte[] roatateImage(String name ) throws IOException{
        BufferedImage image = getBufferedImage(name);
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage rotateImage = new BufferedImage(image.getWidth(),image.getHeight(),image.getType());

        Graphics2D g2 = rotateImage.createGraphics();

        g2.rotate(Math.toRadians(90),width /2, height /2);

        g2.drawImage(image,null,0,0);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(rotateImage, "png", bos);
        byte[] data = bos.toByteArray();

        return data;


    }


    public HttpStatus deleteImage(String name) throws IOException {

        Path imagePath = Paths.get("images/" + name);

        try {
            Files.delete(imagePath);
            return HttpStatus.OK;
        } catch (IOException ioe) {
            System.out.println(ioe);
            return HttpStatus.NOT_FOUND;
        }
    }


}
