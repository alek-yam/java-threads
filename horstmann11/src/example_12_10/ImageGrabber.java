package example_12_10;

import example_12_10.page.PageReader;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageGrabber {
    private static final Pattern IMG_PATTERN = Pattern.compile(
        "[<]\\s*[iI][mM][gG]\\s*[^>]*[sS][rR][cC]\\s*[=]\\s*['\"]([^'\"]*)['\"][^>]*[>]");
    private static final String ROOT_PATH = new File(".").getAbsolutePath();
    private static final String PATH_PATTERN = "{0}\\tmp\\image{1}.png";
    private static final String IMAGE_FORMAT = "PNG";

    private final PageReader pageReader;
    private final ExecutorService executor;

    public ImageGrabber(PageReader pageReader, ExecutorService executor) {
        this.pageReader = pageReader;
        this.executor = executor;
    }

    public CompletableFuture<Void> fromPage(URL url) {
        return pageReader.readAsync(url)
            .thenApply(content -> getImageURLs(content, url))
            .thenCompose(this::getImages)
            .thenAccept(this::saveImages);
    }

    private List<URL> getImageURLs(String pageContent, URL pageUrl) {
        System.out.println("getImageURLs()");
        try {
            var imageUrls = new ArrayList<URL>();
            Matcher matcher = IMG_PATTERN.matcher(pageContent);
            while(matcher.find()) {
                var url = new URL(pageUrl, matcher.group(1));
                System.out.println("Found " + url);
                imageUrls.add(url);
            }
            return imageUrls;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private CompletableFuture<List<BufferedImage>> getImages(List<URL> imageUrls) {
        System.out.println("getImages()");
        return CompletableFuture.supplyAsync(() -> {
            try {
                var images = new ArrayList<BufferedImage>();
                for (URL url : imageUrls) {
                    System.out.print("Loading " + url);
                    BufferedImage image = ImageIO.read(url);
                    if (image != null) {
                        System.out.println(" success");
                        images.add(image);
                    } else {
                        System.out.println(" failed");
                    }
                }
                return images;
            } catch (IOException e) {
                System.out.println("IOException");
                throw new UncheckedIOException(e);
            } catch (Exception e) {
                System.out.println("Exception");
                throw new RuntimeException(e);
            }
        }, executor);
    }

    private void saveImages(List<BufferedImage> images) {
        System.out.println("saveImages()");
        try {
            for (int i = 0; i < images.size(); i++) {
                BufferedImage image = images.get(i);
                String filePath = MessageFormat.format(PATH_PATTERN, ROOT_PATH, i);
                File imageFile = new File(filePath);
                System.out.println("Saving " + imageFile.getAbsolutePath());
                ImageIO.write(images.get(i), IMAGE_FORMAT, imageFile);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
