import java.sql.*;
import java.util.Scanner;

public class TokoRetailCLI {

    // Panggil koneksi dari class Koneksi.java
    static Connection conn = Koneksi.getKoneksi();
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        if (conn == null) {
            System.out.println("Program dihentikan karena database tidak terhubung.");
            return; // Berhenti jika koneksi gagal
        }

        int pilihan = -1;
        while (pilihan != 0) {
            tampilkanMenu();
            System.out.print("Pilihan : ");
            try {
                pilihan = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Masukkan angka yang valid!");
                continue;
            }

            System.out.println();
            switch (pilihan) {
                case 1:
                    tampilSemuaData();
                    break;
                case 2:
                    tambahData();
                    break;
                case 3:
                    cariData();
                    break;
                case 4:
                    ubahData();
                    break;
                case 5:
                    hapusData();
                    break;
                case 0:
                    System.out.println("Keluar dari program...");
                    break;
                default:
                    System.out.println("Pilihan tidak tersedia.");
            }
            System.out.println();
        }

        try {
            conn.close(); // Tutup koneksi saat program selesai
        } catch (SQLException e) {
            System.out.println("Gagal menutup database: " + e.getMessage());
        }
    }

    static void tampilkanMenu() {
        System.out.println("=================================");
        System.out.println("        MENU TOKO RETAIL         ");
        System.out.println("=================================");
        System.out.println("1. Tampil Semua Data");
        System.out.println("2. Tambah Data");
        System.out.println("3. Cari Data");
        System.out.println("4. Ubah Data");
        System.out.println("5. Hapus Data");
        System.out.println("0. Keluar");
        System.out.println("=================================");
    }

    // 1. Menampilkan Data
    static void tampilSemuaData() {
        String sql = "SELECT * FROM barang";
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("======================================================================");
            System.out.println("                     DAFTAR BARANG TOKO RETAIL                        ");
            System.out.println("======================================================================");
            System.out.printf("| %-3s | %-9s | %-25s | %-10s | %-5s |\n", "#", "ID Barang", "Nama Barang", "Harga",
                    "Stok");
            System.out.println("======================================================================");

            int no = 1;
            while (rs.next()) {
                System.out.printf("| %-3d | %-9d | %-25s | %-10d | %-5d |\n",
                        no++, rs.getInt("id_barang"), rs.getString("nama_barang"),
                        rs.getInt("harga"), rs.getInt("stok"));
            }
            System.out.println("======================================================================");
            System.out.println("Total: " + (no - 1) + " barang");

        } catch (SQLException e) {
            System.out.println("Gagal menampilkan data: " + e.getMessage());
        }
    }

    // 2. Menambah Data
    static void tambahData() {
        System.out.println("--- TAMBAH DATA BARANG ---");
        System.out.print("Masukkan Nama Barang  : ");
        String nama = scanner.nextLine();
        System.out.print("Masukkan Harga Barang : ");
        int harga = Integer.parseInt(scanner.nextLine());
        System.out.print("Masukkan Stok Barang  : ");
        int stok = Integer.parseInt(scanner.nextLine());

        String sql = "INSERT INTO barang (nama_barang, harga, stok) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nama);
            pstmt.setInt(2, harga);
            pstmt.setInt(3, stok);
            pstmt.executeUpdate();
            System.out.println("Data berhasil ditambahkan!");
        } catch (SQLException e) {
            System.out.println("Gagal menambah data: " + e.getMessage());
        }
    }

    // 3. Mencari Data
    static void cariData() {
        System.out.println("--- CARI DATA BARANG ---");
        System.out.print("Masukkan ID atau Nama Barang yang dicari: ");
        String keyword = scanner.nextLine();

        String sql = "SELECT * FROM barang WHERE id_barang LIKE ? OR nama_barang LIKE ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n--- Hasil Pencarian ---");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("ID Barang   : " + rs.getInt("id_barang"));
                System.out.println("Nama Barang : " + rs.getString("nama_barang"));
                System.out.println("Harga       : " + rs.getInt("harga"));
                System.out.println("Stok        : " + rs.getInt("stok"));
                System.out.println("-----------------------");
            }
            if (!found)
                System.out.println("Data tidak ditemukan.");
        } catch (SQLException e) {
            System.out.println("Gagal mencari data: " + e.getMessage());
        }
    }

    // 4. Mengubah Data
    static void ubahData() {
        System.out.println("--- UBAH DATA BARANG ---");
        System.out.print("Masukkan ID Barang yang ingin diubah: ");
        int id = Integer.parseInt(scanner.nextLine());

        System.out.print("Masukkan Nama Barang Baru  : ");
        String nama = scanner.nextLine();
        System.out.print("Masukkan Harga Barang Baru : ");
        int harga = Integer.parseInt(scanner.nextLine());
        System.out.print("Masukkan Stok Barang Baru  : ");
        int stok = Integer.parseInt(scanner.nextLine());

        String sql = "UPDATE barang SET nama_barang = ?, harga = ?, stok = ? WHERE id_barang = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nama);
            pstmt.setInt(2, harga);
            pstmt.setInt(3, stok);
            pstmt.setInt(4, id);

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                System.out.println("Data berhasil diubah!");
            } else {
                System.out.println("Data dengan ID tersebut tidak ditemukan.");
            }
        } catch (SQLException e) {
            System.out.println("Gagal mengubah data: " + e.getMessage());
        }
    }

    // 5. Menghapus Data
    static void hapusData() {
        System.out.println("--- HAPUS DATA BARANG ---");
        System.out.print("Masukkan ID Barang yang ingin dihapus: ");
        int id = Integer.parseInt(scanner.nextLine());

        String sql = "DELETE FROM barang WHERE id_barang = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                System.out.println("Data berhasil dihapus!");
            } else {
                System.out.println("Data dengan ID tersebut tidak ditemukan.");
            }
        } catch (SQLException e) {
            System.out.println("Gagal menghapus data: " + e.getMessage());
        }
    }
}