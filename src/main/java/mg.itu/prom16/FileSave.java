package mg.itu.prom16;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

public class FileSave {

    public static String saveFile(Part file, HttpServletRequest request) throws Exception {
        if (file != null) {
            // Chemin vers le dossier webapp de l'application
            String webAppPath = request.getServletContext().getRealPath("/");
            String uploadDir = webAppPath + "uploads";

            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();  // Crée le répertoire si nécessaire
            }

            // Permissions (Windows/Linux)
            setDirectoryPermissions(directory.toPath());

            // Nom original du fichier
            String fileName = Paths.get(file.getSubmittedFileName()).getFileName().toString();
            Path filePath = Paths.get(uploadDir, fileName);

            // S’il existe déjà → générer un autre nom
            filePath = getUniqueFilePath(filePath);

            // Enregistrement du fichier
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, filePath);
            } catch (IOException e) {
                throw new IOException("Impossible d'écrire le fichier: " + filePath, e);
            }

            // Retourner le chemin HTTP relatif
            return request.getContextPath() + "/uploads/" + filePath.getFileName().toString();
        }
        return null;
    }

    private static Path getUniqueFilePath(Path filePath) {
        Path newPath = filePath;
        int count = 1;

        // Séparer nom et extension
        String fileName = filePath.getFileName().toString();
        String name = fileName;
        String extension = "";

        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex > 0) {
            name = fileName.substring(0, dotIndex);
            extension = fileName.substring(dotIndex);
        }

        // Boucler jusqu’à trouver un fichier inexistant
        while (Files.exists(newPath)) {
            String newName = String.format("%s(%d)%s", name, count++, extension);
            newPath = filePath.getParent().resolve(newName);
        }

        return newPath;
    }

    private static void setDirectoryPermissions(Path directoryPath) {
        File directory = directoryPath.toFile();

        directory.setWritable(true, false); 
        directory.setReadable(true, false); 
        directory.setExecutable(true, false);
    }
}
