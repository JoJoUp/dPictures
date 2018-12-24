import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Класс, предназначенный для скачивания картинок с сайта.
 */

    public class DownloadPic {

    public static final String DATA_URL = "\\s*(?<=<img [^>]{0,100}src\\s?=\\s?\")[^>\"]+";
    private static final String IN_FILE_TXT = "src\\inFile.txt";
    private static final String OUT_FILE_TXT = "src\\outFile.txt";
    private static final String PATH_TO_PICTURE = "src\\image";
    private static final int COUNT_PICTURE = 10;

    public static void main(String[] args) {
        try (BufferedReader inFile = new BufferedReader(new FileReader(IN_FILE_TXT));
             BufferedWriter outFile = new BufferedWriter(new FileWriter(OUT_FILE_TXT))) {
            extractMusicURLs(inFile, outFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedReader musicFile = new BufferedReader(new FileReader(OUT_FILE_TXT))) {
            download(musicFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод для скачивания картинок, с помощью чтения и передачи байт.
     *
     * @param strUrl - ссылка на картинку
     * @param file   - имя картинки
     * @throws IOException - ошибка ввода / вывода
     */
    private static void downloadUsingNIO(String strUrl, String file) throws IOException {
        URL url = new URL(strUrl);
        ReadableByteChannel byteChannel = Channels.newChannel(url.openStream());
        FileOutputStream stream = new FileOutputStream(file);
        stream.getChannel().transferFrom(byteChannel, 0, Long.MAX_VALUE);
        stream.close();
        byteChannel.close();
    }

    /**
     * Метод для нахождения ссылок на сайте, путь к которому берется из исходного файла
     * и записи этих ссылок в выходной файл.
     *
     * @param inFile  - исходный файл
     * @param outFile - выходной файл.
     */
    private static void extractMusicURLs(BufferedReader inFile, BufferedWriter outFile) {
        String Url;
        try {
            while ((Url = inFile.readLine()) != null) {
                URL url = new URL(Url);
                String result = fetchHost(url);
                Pattern email_pattern = Pattern.compile(DATA_URL);
                Matcher matcher = email_pattern.matcher(result);
                int i = 0;
                while (matcher.find() && i < COUNT_PICTURE) {
                    outFile.write(matcher.group() + "\r\n");
                    i++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Метод, возвращающий строки, преобразованные из ссылок.
     *
     * @param url - ссылка
     * @return - строка
     * @throws IOException - ошибка ввода/вывода.
     */
    private static String fetchHost(URL url) throws IOException {
        String result;
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            result = bufferedReader.lines().collect(Collectors.joining("\n"));
        }
        return result;
    }

    /**
     * Метод для скачивания картинок.
     *
     * @param pictureFile - путь к выходному файлу.
     */
    private static void download(BufferedReader pictureFile) {
        try {
            String picture;
            int count = 0;
            while ((picture = pictureFile.readLine()) != null) {
                downloadUsingNIO(picture, PATH_TO_PICTURE + String.valueOf(count) + ".jpg");
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}