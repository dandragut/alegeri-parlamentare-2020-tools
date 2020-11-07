package ro.alegeri.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.BufferedSink;
import okio.Okio;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class HttpClient {
    /**
     * Constants
     */
    public static final boolean DEBUG_MODE = false;
    public static final String  USER_AGENT  = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:65.0) Gecko/20100101 Firefox/65.0";

    /*
     * Descarca un fisier de pe Web intr-un fisier local
     * @param url  adresa URL (ca String) de unde se descarca continutul
     * @param file fisierul local in care se salveaza continutul
     */
    public static void descarca(String url, File file) throws Exception {
        descarca(new URL(url), file);
    }

    public static void descarca(URL url, File file) throws Exception {
        // Creaza structura de directoare...
        FileUtils.forceMkdirParent(file);

        // Fisiere binar (e.g. Excel, XML)
        final String extension = FilenameUtils.getExtension(url.getPath());
        if (StringUtils.equalsAnyIgnoreCase(extension, "xls", "xlsx", "xml", "pdf")) {
            descarcaFisier(url, file);
        }
        // Pagini web (e.g. cautari)
        else {
            descarcaPagina(url, file);
        }
    }

    /**
     * Descarca o pagina de web (HTML) intr-un fisier local
     * @param url adresa URL a paginii
     * @param file fisierul local in care se salveaza continutul
     * @throws IOException IOException daca nu se poate salva in fisier
     */
    @SuppressWarnings("ConstantConditions")
    private static void descarcaPagina(URL url, File file) throws IOException {
        // Chrome Driver (internal)
        System.setProperty("webdriver.chrome.driver", HttpClient.class.getClassLoader().getResource("chromedriver.exe").getFile());

        final ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--disable-extensions", "--incognito", "--disable-java");

        // Web Driver...
        WebDriver webDriver = new ChromeDriver(chromeOptions);
        webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        webDriver.navigate().to(url);

        // Save file...
        FileUtils.writeStringToFile(file, webDriver.getPageSource(), "UTF-8");

        // Cleanup...
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        webDriver.quit();
    }

    /*
     * Descarca un fisier binar (e.g. Excel) de pe Web intr-un fisier local.
     * @param url  adresa URL de unde se descarca continutul
     * @param file fisierul local in care se salveaza continutul
     * @throws
     */
    public static void descarcaFisier(URL url, File file) throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[] { new TrustEveryoneManager() }, null);

        // Debug...
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(DEBUG_MODE ? HttpLoggingInterceptor.Level.HEADERS : HttpLoggingInterceptor.Level.NONE);

        // Client...
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .sslSocketFactory(sslContext.getSocketFactory(), new TrustEveryoneManager())
                .addInterceptor(httpLoggingInterceptor)
                .build();

        // Request...
        Request  request  = new Request.Builder()
                .url(url)
                .header("User-Agent", USER_AGENT)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        BufferedSink sink = Okio.buffer(Okio.sink(file));
        sink.writeAll(response.body().source());
        sink.close();
    }

    /*
     * Very trusty TrustManager (do not check certificate)...
     */
    private final static class TrustEveryoneManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) { }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) { }

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[]{};
        }
    }
}