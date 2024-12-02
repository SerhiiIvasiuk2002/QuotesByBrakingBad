package com.botquotes.botquotesbybreakingbad.util;

import com.botquotes.botquotesbybreakingbad.exception.ImageNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ImageUtil {
    private final WebClient webClient;

    public String findImage(String searchQuery) throws ImageNotFoundException {
        List<String> imageUrls = fetchImageUrls(searchQuery);

        for (String imageUrl : imageUrls) {
            if (!imageUrl.contains("google")) {
                System.out.println(imageUrl);
                return imageUrl;
            }
        }

        throw new ImageNotFoundException("Can't find image for: " + searchQuery);
    }

    private List<String> fetchImageUrls(String searchQuery) {
        try {
            HtmlPage page = webClient.getPage("https://www.google.com/search?hl=en&tbm=isch&q=" + searchQuery);
            List<HtmlImage> images = page.getByXPath("//img[@data-src or @srcset or @src]");
            return images.stream()
                    .map(img -> {
                        String src = img.getAttribute("data-src");
                        if (src.isEmpty()) {
                            src = img.getSrc();
                        }
                        if (img.getAttribute("srcset") != null && !img.getAttribute("srcset").isEmpty()) {
                            src = img.getAttribute("srcset").split(",")[0].split(" ")[0];
                        }
                        return src;
                    })
                    .toList();
        } catch (IOException e) {
            return List.of();
        } finally {
            webClient.close();
        }
    }

    private byte[] downloadImage(String imageUrl) throws ImageNotFoundException {
        try (InputStream inputStream = new URL(imageUrl).openStream();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[2048];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }
}

