import java.io.*;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadAndPlay {
    public static void main(String[] args) {
        try {
            // Чтение файла с URL и путями для сохранения файлов
            BufferedReader reader = new BufferedReader(new FileReader("urls.txt"));
            String line;
            ExecutorService executor = Executors.newFixedThreadPool(2);

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length != 2) {
                    System.out.println("Неверный формат строки в файле.");
                    continue;
                }

                String url = parts[0];
                String savePath = parts[1];

                executor.submit(() -> {
                    try {
                        downloadFile(url, savePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

            executor.shutdown();

            // Ожидание завершения всех задач перед воспроизведением mp3
            while (!executor.isTerminated()) {
                Thread.sleep(1000);
            }

            // После завершения загрузки всех файлов, воспроизводим mp3
            playMp3("C:\\Users\\Пользователь\\Documents\\DownloadAndPlay\\music.mp3");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void downloadFile(String url, String savePath) throws IOException {
        try (InputStream in = new URL(url).openStream();
             OutputStream out = new FileOutputStream(savePath)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }

    public static void playMp3(String mp3FilePath) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "start", "wmplayer", mp3FilePath);
            Process process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
