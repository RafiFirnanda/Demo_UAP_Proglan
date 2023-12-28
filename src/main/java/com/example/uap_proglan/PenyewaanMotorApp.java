package com.example.uap_proglan;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Aplikasi Penyewaan Motor.
 * Memungkinkan pengguna untuk menyewa motor dan menyimpan struk penyewaan ke dalam file teks.
 *
 * @author Muhammad Rafi Firnanda & Tubagus Syahrijal Amri
 * @version 1.0
 */
public class PenyewaanMotorApp extends Application {

    private ObservableList<String> motorOptions = FXCollections.observableArrayList(
            "Honda CBR500R", "Yamaha MT-07", "Kawasaki Ninja 650", "Suzuki SV650");

    private ObservableList<Integer> hargaOptions = FXCollections.observableArrayList(
            150000, 120000, 130000, 110000);

    /**
     * Metode utama untuk menjalankan aplikasi JavaFX.
     *
     * @param args Argumen baris perintah (tidak digunakan).
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Metode untuk menginisialisasi dan menampilkan antarmuka pengguna.
     *
     * @param primaryStage Objek Stage utama.
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Aplikasi Penyewaan Motor");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));

        Label labelNamaMotor = new Label("Nama Motor:");
        ComboBox<String> motorComboBox = new ComboBox<>(motorOptions);
        Label labelHarga = new Label("Harga Sewa per Hari:");
        Label labelHargaValue = new Label();
        Label labelHariSewa = new Label("Jumlah Hari Sewa:");
        TextField textFieldHariSewa = new TextField();

        motorComboBox.setOnAction(e -> {
            int selectedIndex = motorComboBox.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < hargaOptions.size()) {
                int hargaSewa = hargaOptions.get(selectedIndex);
                labelHargaValue.setText(String.valueOf(hargaSewa));
            }
        });

        Button btnSubmit = new Button("Submit");
        grid.add(labelNamaMotor, 0, 0);
        grid.add(motorComboBox, 1, 0);
        grid.add(labelHarga, 0, 1);
        grid.add(labelHargaValue, 1, 1);
        grid.add(labelHariSewa, 0, 2);
        grid.add(textFieldHariSewa, 1, 2);
        grid.add(btnSubmit, 1, 3);

        btnSubmit.setOnAction(e -> {
            try {
                String namaMotor = motorComboBox.getValue();
                if (namaMotor == null || namaMotor.isEmpty()) {
                    throw new InvalidInputException("Pilih motor yang tersedia.");
                }

                int hargaSewa = Integer.parseInt(labelHargaValue.getText());
                int hariSewa = Integer.parseInt(textFieldHariSewa.getText());

                validateInput(namaMotor, hargaSewa, hariSewa);

                int totalBiaya = hargaSewa * hariSewa;
                LocalDate tanggalSewa = LocalDate.now();

                showResultDialog(namaMotor, hargaSewa, hariSewa, totalBiaya, tanggalSewa);
                saveToStrukToFile(namaMotor, hargaSewa, hariSewa, totalBiaya, tanggalSewa);
            } catch (NumberFormatException | InvalidInputException ex) {
                showAlert("Error", ex.getMessage());
            }
        });

        Scene scene = new Scene(grid, 400, 250);
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    /**
     * Memvalidasi input dari pengguna.
     *
     * @param namaMotor Nama motor yang dipilih.
     * @param hargaSewa Harga sewa per hari.
     * @param hariSewa Jumlah hari sewa.
     * @throws InvalidInputException Jika input tidak valid.
     */
    private void validateInput(String namaMotor, int hargaSewa, int hariSewa) throws InvalidInputException {
        if (hariSewa < 1 || hariSewa > 7) {
            throw new InvalidInputException("Masukkan jumlah hari sewa antara 1-7 hari.");
        }
    }

    /**
     * Menampilkan dialog informasi.
     *
     * @param title   Judul dialog.
     * @param message Pesan yang ditampilkan.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Menampilkan dialog informasi dengan judul "Sukses".
     *
     * @param title   Judul dialog.
     * @param message Pesan yang ditampilkan.
     */
    private void showAlert2(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Menampilkan dialog hasil perhitungan penyewaan motor.
     *
     * @param namaMotor   Nama motor yang dipilih.
     * @param hargaSewa   Harga sewa per hari.
     * @param hariSewa    Jumlah hari sewa.
     * @param totalBiaya  Total biaya penyewaan.
     * @param tanggalSewa Tanggal sewa.
     */
    private void showResultDialog(String namaMotor, int hargaSewa, int hariSewa, int totalBiaya, LocalDate tanggalSewa) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Hasil Perhitungan");
        alert.setHeaderText(null);
        alert.setContentText("Motor " + namaMotor + "\n" +
                "Harga Sewa per Hari: " + hargaSewa + "\n" +
                "Jumlah Hari Sewa: " + hariSewa + "\n" +
                "Total Biaya: " + totalBiaya + "\n" +
                "Tanggal Sewa: " + tanggalSewa.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        alert.showAndWait();
    }

    /**
     * Menyimpan struk penyewaan ke dalam file teks.
     *
     * @param namaMotor   Nama motor yang dipilih.
     * @param hargaSewa   Harga sewa per hari.
     * @param hariSewa    Jumlah hari sewa.
     * @param totalBiaya  Total biaya penyewaan.
     * @param tanggalSewa Tanggal sewa.
     */
    private void saveToStrukToFile(String namaMotor, int hargaSewa, int hariSewa, int totalBiaya, LocalDate tanggalSewa) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Simpan Struk ke File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                writer.newLine();
                writer.write("Struk Penyewaan Motor");
                writer.newLine();
                writer.write("Motor: " + namaMotor);
                writer.newLine();
                writer.write("Harga Sewa per Hari: " + hargaSewa);
                writer.newLine();
                writer.write("Jumlah Hari Sewa: " + hariSewa);
                writer.newLine();
                writer.write("Total Biaya: " + totalBiaya);
                writer.newLine();
                writer.write("Tanggal Sewa: " + tanggalSewa.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                writer.newLine();

                showAlert2("Sukses", "Struk berhasil disimpan ke file: " + file.getAbsolutePath());
            } catch (IOException e) {
                showAlert("Error", "Gagal menyimpan file.");
                e.printStackTrace();
            }
        }
    }

    /**
     * Kelas pengecualian khusus untuk menangani input tidak valid.
     */
    private static class InvalidInputException extends Exception {
        /**
         * Konstruktor untuk membuat objek InvalidInputException dengan pesan tertentu.
         *
         * @param message Pesan pengecualian.
         */
        public InvalidInputException(String message) {
            super(message);
        }
    }
}
