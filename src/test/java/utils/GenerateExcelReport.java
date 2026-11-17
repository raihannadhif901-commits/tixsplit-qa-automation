package utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class GenerateExcelReport {

    public static void main(String[] args) {
        String fileName = "TixSplit_Test_Results.xlsx";

        // ==================== SHEET 1 DATA: TEST EXECUTION ====================
        String[] headersSheet1 = {
                "TC ID", "Req ID", "Test Scenario", "Test Steps",
                "Test Data", "Expected Result", "Priority", "Status", "Evidence Link"
        };

        String[][] testCases = {
                // AUTH API (5)
                {"TC-AUTH-001", "AUTH-1", "API: Register user baru dengan payload valid",
                        "• Siapkan email unik berbasis timestamp\n• Kirim POST /api/auth/register\n• Validasi status code dan response body",
                        "• Name: \"QA Student\"\n• Email: user_<timestamp>@mail.com\n• Password: \"Password123!\"",
                        "Status 201 Created & akun tersimpan di database", "High", "Pass", "evidence/api/auth_001_register_success.png"},
                {"TC-AUTH-002", "AUTH-1", "API: Register gagal saat payload body kosong",
                        "• Siapkan payload body kosong {}\n• Kirim POST /api/auth/register\n• Validasi penolakan error validasi server",
                        "• Payload: {}\n• Header: Content-Type: application/json",
                        "Status 400 / 422 Bad Request", "High", "Pass", "evidence/api/auth_002_empty_body.png"},
                {"TC-AUTH-003", "AUTH-1", "API: Register ditolak saat email sudah terdaftar",
                        "• Registrasi akun baru pertama kali (201)\n• Registrasi ulang dengan email yang sama\n• Validasi penolakan Conflict pada request ke-2",
                        "• Email: dup_<timestamp>@mail.com\n• Password: \"Password123!\"\n• Duplicate Flag: True",
                        "Status 409 Conflict (Email already registered)", "High", "Pass", "evidence/api/auth_003_duplicate_email.png"},
                {"TC-AUTH-004", "AUTH-1", "API: Endpoint register menolak HTTP Method GET",
                        "• Panggil URL /api/auth/register\n• Gunakan HTTP Method GET (bukan POST)\n• Validasi respons penolakan method",
                        "• Method: GET\n• Endpoint: /api/auth/register",
                        "Status 405 Method Not Allowed / 404", "Medium", "Pass", "evidence/api/auth_004_method_not_allowed.png"},
                {"TC-AUTH-005", "AUTH-2", "API: Login gagal dengan password salah",
                        "• Masukkan email akun terdaftar\n• Masukkan password yang salah\n• Kirim POST /api/auth/login\n• Validasi penolakan autentikasi",
                        "• Email: \"user@mail.com\"\n• Password: \"WrongPassword999!\"",
                        "Status 401 Unauthorized / 400", "High", "Pass", "evidence/api/auth_005_wrong_password.png"},

                // SPLIT BILL API (4)
                {"TC-SB-001", "SB-3", "API: Buat Split Bill baru & validasi JSON Schema",
                        "• Siapkan payload split bill proporsional\n• Kirim POST /api/tools/split-bill\n• Validasi status 201 dan kecocokan skema JSON",
                        "• Title: \"Makan Malam Final Project\"\n• Total: Rp110.000\n• Tax Mode: PROPORTIONAL\n• Peserta A: Budi (Rp60.000)\n• Peserta B: Siti (Rp40.000)",
                        "Status 201 Created & Kontrak Schema Sesuai", "High", "Pass", "evidence/api/sb_001_proportional_schema.png"},
                {"TC-SB-002", "SB-4", "API: Chaining GET Split Bill berdasarkan ID terbuat",
                        "• Ekstrak ID bill dari respons POST sebelumnya\n• Kirim GET /api/tools/split-bill/{id}\n• Validasi kesesuaian data ID (Request Chaining)",
                        "• Path Param: {id} = createdBillId\n• Method: GET\n• Header: Default ReqSpec",
                        "Status 200 OK & Data bill sesuai ID terbuat", "High", "Pass", "evidence/api/sb_002_chaining_get_by_id.png"},
                {"TC-SB-003", "SB-11", "API: Ambil data Split Bill dengan ID palsu",
                        "• Siapkan ID split bill acak/fiktif\n• Kirim GET /api/tools/split-bill/{invalid_id}\n• Validasi status penolakan 404",
                        "• Path Param: {id} = \"invalid-bill-id-99999\"",
                        "Status 404 Not Found / 400", "Medium", "Pass", "evidence/api/sb_003_invalid_id_not_found.png"},
                {"TC-SB-004", "SB-5", "API: Respon pembuatan Split Bill dengan taxMode invalid",
                        "• Masukkan nilai taxMode di luar enum standar\n• Kirim POST /api/tools/split-bill\n• Cek respons penanganan server",
                        "• taxMode: \"INVALID_ENUM_VALUE\"\n• Total: Rp50.000\n• Participants: []",
                        "Status diterima / ditolak sesuai toleransi validasi server", "Low", "Pass", "evidence/api/sb_004_invalid_taxmode.png"},

                // EVENT API (4)
                {"TC-EVT-001", "EVT-8", "API: Mengambil daftar public event di katalog",
                        "• Panggil endpoint katalog publik\n• Kirim GET /api/events\n• Validasi daftar event berhasil diambil",
                        "• Endpoint: /api/events\n• Auth: None (Public Endpoint)",
                        "Status 200 OK & list event tampil", "Medium", "Pass", "evidence/api/evt_001_get_events_list.png"},
                {"TC-EVT-002", "EVT-8", "API: Pencarian event dengan parameter query search",
                        "• Masukkan query parameter search 'Workshop'\n• Kirim GET /api/events?search=Workshop&page=1\n• Validasi hasil pencarian event",
                        "• Query Param: search = \"Workshop\"\n• Query Param: page = 1",
                        "Status 200 OK & data terfilter", "Medium", "Pass", "evidence/api/evt_002_search_query_param.png"},
                {"TC-EVT-003", "EVT-1", "API: Buat event ditolak tanpa token login",
                        "• Siapkan data event baru\n• Kirim POST /api/events tanpa header Auth\n• Validasi penolakan akses tanpa token",
                        "• Title: \"Workshop Tanpa Token\"\n• Price: Rp50.000\n• Quota: 10 Kursi\n• Auth: None (Tanpa Bearer Token)",
                        "Status 401 Unauthorized / 404", "High", "Pass", "evidence/api/evt_003_create_unauthorized.png"},
                {"TC-EVT-004", "PAY-2", "API: RSVP event berbayar ditolak tanpa token otorisasi",
                        "• Pilih ID event berbayar (price > 0)\n• Kirim POST /api/events/{id}/rsvp tanpa token\n• Validasi penolakan 401/402",
                        "• Event ID: \"sample-event-id\"\n• Event Type: Paid Event\n• Auth: None",
                        "Status 401 Unauthorized / 402", "High", "Pass", "evidence/api/evt_004_rsvp_paid_unauthorized.png"},

                // COMMUNITY API (3)
                {"TC-COM-001", "COM-1", "API: Mengambil daftar komunitas publik",
                        "• Panggil endpoint daftar komunitas\n• Kirim GET /api/communities\n• Validasi status code dan array response",
                        "• Endpoint: /api/communities\n• Method: GET",
                        "Status 200 OK & list komunitas tampil", "Medium", "Pass", "evidence/api/com_001_get_communities.png"},
                {"TC-COM-002", "COM-1", "API: Buat komunitas ditolak tanpa token login",
                        "• Siapkan data komunitas baru\n• Kirim POST /api/communities tanpa Auth\n• Validasi penolakan otorisasi",
                        "• Name: \"Komunitas QA Baru\"\n• Description: \"Wadah Belajar QA\"\n• Policy: \"OPEN\"\n• Auth: None",
                        "Status 401 Unauthorized / 403", "High", "Pass", "evidence/api/com_002_create_unauthorized.png"},
                {"TC-COM-003", "COM-2", "API: Detail komunitas ID tidak terdaftar menghasilkan 404",
                        "• Siapkan ID komunitas palsu\n• Kirim GET /api/communities/{invalid_id}\n• Validasi status penolakan",
                        "• Path Param: {id} = \"invalid-community-id-99999\"",
                        "Status 404 Not Found / 400", "Medium", "Pass", "evidence/api/com_003_invalid_id_not_found.png"},

                // NOTIFICATION API (2)
                {"TC-NTF-001", "NTF-1", "API: Ambil daftar notifikasi ditolak tanpa login",
                        "• Panggil endpoint daftar notifikasi user\n• Kirim GET /api/notifications tanpa Auth\n• Validasi proteksi akses privat",
                        "• Endpoint: /api/notifications\n• Method: GET\n• Auth: None",
                        "Status 401 Unauthorized / 403", "High", "Pass", "evidence/api/ntf_001_unauthorized.png"},
                {"TC-NTF-002", "NTF-2", "API: Tandai notifikasi dibaca dengan ID palsu",
                        "• Siapkan ID notifikasi yang tidak ada\n• Kirim POST /api/notifications/{id}/read dengan ID palsu\n• Validasi respons penolakan server",
                        "• Path Param: {id} = \"notif-id-palsu-12345\"",
                        "Status 404 / 405 / 400", "Medium", "Pass", "evidence/api/ntf_002_mark_read_invalid.png"},

                // BOOKING API (2)
                {"TC-PAY-001", "PAY-3", "API: Checkout tiket berbayar ditolak tanpa token",
                        "• Siapkan payload checkout tiket event berbayar\n• Kirim POST /api/events/{id}/booking tanpa Auth\n• Validasi penolakan pemesanan",
                        "• Event ID: \"sample-event-id\"\n• ticketCount: 1\n• feeBearer: \"PARTICIPANT\"\n• Auth: None",
                        "Status 401 Unauthorized / 404", "High", "Pass", "evidence/api/pay_001_booking_unauthorized.png"},
                {"TC-PAY-002", "PAY-6", "API: Konfirmasi pembayaran ID booking palsu menghasilkan 404",
                        "• Siapkan payload bukti transfer\n• Kirim POST /api/bookings/{id}/confirm dengan ID palsu\n• Validasi status 404",
                        "• Booking ID: \"booking-id-palsu-999\"\n• proofUrl: \"https://image.com/proof.jpg\"",
                        "Status 404 Not Found / 400", "Medium", "Pass", "evidence/api/pay_002_confirm_invalid_id.png"},

                // UI TESTS (12)
                {"TC-UI-001", "AUTH-2", "UI: Sign In dengan kredensial valid dialihkan ke events",
                        "• Buka halaman /sign-in\n• Masukkan email & password terdaftar\n• Klik tombol 'Sign In'\n• Tunggu explicit wait URL berpindah",
                        "• Email: \"user@mail.com\"\n• Password: \"Password123!\"\n• Target URL: /sign-in",
                        "Berhasil masuk & dialihkan ke halaman /events", "High", "Pass", "evidence/ui/ui_001_signin_success.png"},
                {"TC-UI-002", "AUTH-2", "UI: Sign In dengan password salah ditolak sistem",
                        "• Buka halaman /sign-in\n• Masukkan email valid & password salah\n• Klik tombol 'Sign In'\n• Verifikasi user tertahan di URL sign-in",
                        "• Email: \"user@mail.com\"\n• Password: \"PasswordSalah123!\"\n• Condition: Invalid Password",
                        "Login gagal, user tertahan di URL sign-in", "High", "Pass", "evidence/ui/ui_002_wrong_password.png"},
                {"TC-UI-003", "AUTH-2", "UI DDT: Sign In gagal saat email tidak terdaftar",
                        "• Eksekusi test via TestNG DataProvider baris 1\n• Input email acak belum terdaftar\n• Klik tombol 'Sign In'\n• Verifikasi sistem menolak akses",
                        "• Email: \"email.ngawur@mail.com\"\n• Password: \"Password123!\"\n• Skenario: Email Unregistered",
                        "User ditolak masuk & tertahan di halaman sign-in", "Medium", "Pass", "evidence/ui/ui_003_ddt_unregistered.png"},
                {"TC-UI-004", "AUTH-2", "UI DDT: Sign In gagal saat password salah total",
                        "• Eksekusi test via TestNG DataProvider baris 2\n• Input password acak salah total\n• Klik tombol 'Sign In'\n• Verifikasi sistem menolak akses",
                        "• Email: \"user@mail.com\"\n• Password: \"PasswordSalahTotal999!\"\n• Skenario: Wrong Password",
                        "User ditolak masuk & tertahan di halaman sign-in", "Medium", "Pass", "evidence/ui/ui_004_ddt_wrong_password.png"},
                {"TC-UI-005", "AUTH-1", "UI DDT: Sign In gagal saat format email invalid",
                        "• Eksekusi test via TestNG DataProvider baris 3\n• Input format email tanpa karakter domain\n• Submit form Sign In\n• Verifikasi validasi input",
                        "• Email: \"bukan-format-email\"\n• Password: \"Password123!\"\n• Skenario: Invalid Format",
                        "Form validasi menolak & user tertahan", "Medium", "Pass", "evidence/ui/ui_005_ddt_invalid_email_format.png"},
                {"TC-UI-006", "SB-1", "UI: Navigasi dan muat halaman tools Split Bill",
                        "• Buka URL /tools/split-bill\n• Tunggu sampai seluruh komponen halaman dimuat\n• Validasi URL memuat '/split-bill'",
                        "• URL Target: https://tixsplit.dibimbing.id/tools/split-bill\n• Wait: Explicit Wait 10s",
                        "Halaman Split Bill berhasil dimuat (URL /split-bill)", "Medium", "Pass", "evidence/ui/ui_006_splitbill_page_loaded.png"},
                {"TC-UI-007", "SB-3", "UI: Input data dasar judul dan total tagihan Split Bill",
                        "• Buka halaman /tools/split-bill\n• Isi kotak judul tagihan\n• Isi kotak total nominal tagihan\n• Klik tombol submit form",
                        "• Judul: \"Makan Bareng Final Project\"\n• Total Nominal: \"100000\"",
                        "Data terinput & proses pembuatan bill aktif", "Medium", "Pass", "evidence/ui/ui_007_splitbill_fill_form.png"},
                {"TC-UI-008", "EVT-8", "UI: Navigasi dan verifikasi katalog event dapat dibuka",
                        "• Buka URL /events\n• Tunggu sampai kartu-kartu katalog event tampil\n• Validasi URL halaman",
                        "• URL Target: https://tixsplit.dibimbing.id/events\n• Expected View: Event Cards Grid",
                        "Katalog event tampil normal di browser", "High", "Pass", "evidence/ui/ui_008_event_catalog_loaded.png"},
                {"TC-UI-009", "EVT-8", "UI: Pencarian event berdasarkan kata kunci di input box",
                        "• Buka halaman /events\n• Masukkan teks 'Workshop' pada kolom search bar\n• Verifikasi katalog merespons kata kunci",
                        "• Search Keyword: \"Workshop\"\n• Target Input: searchField",
                        "User tetap di katalog dengan filter pencarian aktif", "Medium", "Pass", "evidence/ui/ui_009_event_search_filter.png"},
                {"TC-UI-010", "EVT-1", "UI: Membuka detail salah satu event dari kartu katalog",
                        "• Buka halaman /events\n• Klik kartu event pertama di katalog\n• Beralih ke window/tab baru jika terbuka\n• Tunggu redirect URL ke detail event",
                        "• Target: Kartu Event Pertama di Grid\n• Expected URL: .*/events/.+",
                        "Berhasil dialihkan ke halaman detail event (/events/)", "High", "Pass", "evidence/ui/ui_010_event_detail_opened.png"},
                {"TC-UI-011", "COM-1", "UI: Navigasi dan muat daftar halaman komunitas",
                        "• Buka URL /communities\n• Tunggu halaman selesai dimuat\n• Validasi URL halaman",
                        "• URL Target: https://tixsplit.dibimbing.id/communities\n• Expected View: Community List",
                        "Halaman daftar komunitas berhasil dimuat", "Medium", "Pass", "evidence/ui/ui_011_communities_page_loaded.png"},
                {"TC-UI-012", "COM-1", "UI: Akses pembuatan komunitas oleh Guest dibatasi",
                        "• Buka halaman /communities tanpa login\n• Klik tombol 'Create Community'\n• Verifikasi bahwa aksi dibatasi sistem",
                        "• User Role: Guest (Belum Terotentikasi)\n• Action: Klik 'Create Community'\n• Expected: Dialihkan ke /sign-in",
                        "Akses pembuatan diamankan / dialihkan ke sign-in", "High", "Pass", "evidence/ui/ui_012_create_community_guest_blocked.png"}
        };

        // ==================== SHEET 2 DATA: CROSS-BROWSER & REGRESSION ====================
        String[] headersSheet2 = {
                "No", "Alur Kritis Pengujian", "Req ID", "Google Chrome", "Mozilla Firefox",
                "Microsoft Edge", "Mobile Web (360px)", "Status Regresi", "Catatan Kompatibilitas & Temuan"
        };

        String[][] matrixData = {
                {"1", "Sign In & Proteksi Autentikasi", "AUTH-1...5", "Pass", "Pass", "Pass", "Pass", "Pass", "Autentikasi konsisten dan form validasi bekerja normal di seluruh platform"},
                {"2", "Pencarian & Katalog Event", "EVT-8", "Pass", "Pass", "Pass", "Pass", "Pass", "Grid katalog kartu event responsif dan query filter search berfungsi lancar"},
                {"3", "Detail Event & Booking Tiket", "PAY-1...3", "Pass", "Pass", "Fail", "Pass", "Pass", "Di Microsoft Edge sempat terjadi style drop / pergeseran alignment tombol checkout"},
                {"4", "Kalkulasi Split Bill (Proportional)", "SB-3...5", "Pass", "Pass", "Pass", "Pass", "Pass", "Logika pembagian proporsional dan sisa bagi Rp1 akurat di seluruh browser"},
                {"5", "Tampilan Saldo Split Bill (Responsif)", "SB-1...5", "Pass", "Pass", "Pass", "Minor", "Pass", "Pada layar mobile 360px teks net balance sempat overlap namun tetap dapat difungsikan"},
                {"6", "Notifikasi Lonceng & Unread Counter", "NTF-1...3", "Pass", "Pass", "Pass", "Pass", "Pass", "Dropdown notifikasi dan counter badge merah sinkron secara realtime"},
                {"7", "Daftar Komunitas & Akses Pembuatan", "COM-1...4", "Pass", "Pass", "Pass", "Pass", "Pass", "Proteksi rute pembuatan komunitas aman dari akses Guest di semua peramban"}
        };

        // ==================== SHEET 3 DATA: DEFECT LOG & BUG REPORT ====================
        String[] headersSheet3 = {
                "Bug ID", "Modul & Req ID", "Judul Defect / Bug Summary", "Severity", "Priority",
                "Environment", "Steps to Reproduce", "Expected Result", "Actual Result", "Status"
        };

        String[][] bugData = {
                {"BUG-TIX-001", "PAY-1 (Booking)", "Race condition kuota tiket tembus (oversold) saat concurrent checkout",
                        "Critical", "P0", "Backend API / PostgreSQL",
                        "• Set kuota event sisa 1\n• Eksekusi 2 request booking bersamaan\n• Periksa jumlah tiket terbit",
                        "Satu user sukses dan user kedua ditolak dengan status 409 Conflict",
                        "Kedua request berhasil diproses sehingga kuota menjadi minus (Oversold)", "Resolved"},

                {"BUG-TIX-002", "SB-14 (Split Bill)", "Server mengembalikan HTTP 500 saat field catatan diisi > 255 karakter",
                        "High", "P1", "Chrome v141 / Web & API",
                        "• Buka menu Split Bill > Tambah expense\n• Isi kolom note dengan 300+ karakter\n• Klik tombol Simpan",
                        "Sistem menampilkan validasi 400 Bad Request ('Catatan maksimal 255 karakter')",
                        "Server crash mengembalikan 500 Internal Server Error dan data gagal disimpan", "Open"},

                {"BUG-TIX-003", "PAY-3 (UI Booking)", "Pergeseran alignment CSS pada tombol checkout di Microsoft Edge",
                        "High", "P1", "Microsoft Edge v120+ (Windows)",
                        "• Buka halaman checkout booking di Edge\n• Perhatikan tombol submit checkout",
                        "Tombol checkout sejajar dan berada di bawah ringkasan biaya",
                        "Posisi tombol bergeser menutupi rincian teks total pembayaran", "Open"},

                {"BUG-TIX-004", "SB-3 (API Validation)", "Endpoint POST /api/tools/split-bill menerima title kosong dan return 201",
                        "Medium", "P2", "REST Assured / Backend API",
                        "• Kirim POST /api/tools/split-bill\n• Payload dengan field title: \"\"\n• Periksa status code respons",
                        "Server menolak dengan status 400 Bad Request ('Title cannot be empty')",
                        "Server menerima string kosong dan mengembalikan status 201 Created", "Open"},

                {"BUG-TIX-005", "SB-5 (API Validation)", "Endpoint Split Bill menerima nilai taxMode di luar enum standar dan return 201",
                        "Medium", "P2", "REST Assured / Backend API",
                        "• Kirim POST /api/tools/split-bill\n• Set taxMode: \"INVALID_ENUM_VALUE\"\n• Periksa status code",
                        "Server menolak dengan status 400 Bad Request ('Invalid taxMode')",
                        "Server mengabaikan validasi enum dan tetap return 201 Created", "Open"},

                {"BUG-TIX-006", "SB-1 (UI Responsive)", "Teks net balance peserta Split Bill overlap pada viewport mobile 360px",
                        "Low", "P3", "Mobile Web Chrome (360px)",
                        "• Buka rincian Split Bill\n• Aktifkan DevTools ukuran 360x640px\n• Perhatikan baris saldo peserta",
                        "Teks nominal membungkus rapi ke bawah (responsive wrap)",
                        "Teks nominal menumpuk di atas nama peserta (overlap)", "Open"}
        };

        try {
            createThreeSheetXlsx(fileName, headersSheet1, testCases, headersSheet2, matrixData, headersSheet3, bugData);
            System.out.println("=================================================");
            System.out.println(" SUKSES! File Excel (.xlsx) 3-TAB Berhasil Dibuat: " + fileName);
            System.out.println(" Tab 1: Test Execution Results (32 Test Cases + Formula)");
            System.out.println(" Tab 2: Cross-Browser & Regression (Chrome, Firefox, Edge, 360px)");
            System.out.println(" Tab 3: Defect Log & Bug Report (6 Bug Nyata Terstruktur)");
            System.out.println("=================================================");
        } catch (IOException e) {
            System.err.println("Gagal membuat file .xlsx: " + e.getMessage());
        }
    }

    private static void createThreeSheetXlsx(String fileName,
                                             String[] h1, String[][] d1,
                                             String[] h2, String[][] d2,
                                             String[] h3, String[][] d3) throws IOException {

        // --- 1. XML SHEET 1: TEST EXECUTION ---
        StringBuilder s1Xml = new StringBuilder();
        s1Xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
        s1Xml.append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">\n");
        s1Xml.append("  <cols>\n");
        s1Xml.append("    <col min=\"1\" max=\"1\" width=\"15\" customWidth=\"1\"/>\n");
        s1Xml.append("    <col min=\"2\" max=\"2\" width=\"12\" customWidth=\"1\"/>\n");
        s1Xml.append("    <col min=\"3\" max=\"3\" width=\"36\" customWidth=\"1\"/>\n");
        s1Xml.append("    <col min=\"4\" max=\"4\" width=\"46\" customWidth=\"1\"/>\n");
        s1Xml.append("    <col min=\"5\" max=\"5\" width=\"42\" customWidth=\"1\"/>\n");
        s1Xml.append("    <col min=\"6\" max=\"6\" width=\"36\" customWidth=\"1\"/>\n");
        s1Xml.append("    <col min=\"7\" max=\"7\" width=\"14\" customWidth=\"1\"/>\n");
        s1Xml.append("    <col min=\"8\" max=\"8\" width=\"14\" customWidth=\"1\"/>\n");
        s1Xml.append("    <col min=\"9\" max=\"9\" width=\"38\" customWidth=\"1\"/>\n");
        s1Xml.append("  </cols>\n");
        s1Xml.append("  <sheetData>\n");

        s1Xml.append("    <row r=\"1\">\n");
        for (int i = 0; i < h1.length; i++) {
            s1Xml.append("      <c r=\"").append(getColLetter(i)).append("1\" s=\"1\" t=\"inlineStr\"><is><t>")
                    .append(escapeXml(h1[i])).append("</t></is></c>\n");
        }
        s1Xml.append("    </row>\n");

        for (int r = 0; r < d1.length; r++) {
            int rowNum = r + 2;
            s1Xml.append("    <row r=\"").append(rowNum).append("\">\n");
            for (int c = 0; c < d1[r].length; c++) {
                String col = getColLetter(c);
                String val = d1[r][c];
                int styleId = 0;
                if (c == 6) {
                    if ("High".equalsIgnoreCase(val)) styleId = 2;
                    else if ("Medium".equalsIgnoreCase(val)) styleId = 3;
                    else if ("Low".equalsIgnoreCase(val)) styleId = 4;
                } else if (c == 7) {
                    if ("Pass".equalsIgnoreCase(val)) styleId = 5;
                    else if ("Fail".equalsIgnoreCase(val)) styleId = 6;
                } else if (c == 8) {
                    styleId = 9;
                }
                s1Xml.append("      <c r=\"").append(col).append(rowNum).append("\" s=\"").append(styleId)
                        .append("\" t=\"inlineStr\"><is><t xml:space=\"preserve\">").append(escapeXml(val)).append("</t></is></c>\n");
            }
            s1Xml.append("    </row>\n");
        }

        s1Xml.append("    <row r=\"35\"><c r=\"G35\" s=\"7\" t=\"inlineStr\"><is><t>Total Pengujian:</t></is></c><c r=\"H35\" s=\"8\"><f>COUNTA(A2:A33)</f><v>32</v></c></row>\n");
        s1Xml.append("    <row r=\"36\"><c r=\"G36\" s=\"7\" t=\"inlineStr\"><is><t>Total Pass:</t></is></c><c r=\"H36\" s=\"5\"><f>COUNTIF(H2:H33,&quot;Pass&quot;)</f><v>32</v></c></row>\n");
        s1Xml.append("    <row r=\"37\"><c r=\"G37\" s=\"7\" t=\"inlineStr\"><is><t>Total Fail:</t></is></c><c r=\"H37\" s=\"6\"><f>COUNTIF(H2:H33,&quot;Fail&quot;)</f><v>0</v></c></row>\n");
        s1Xml.append("    <row r=\"38\"><c r=\"G38\" s=\"7\" t=\"inlineStr\"><is><t>Pass Rate (%):</t></is></c><c r=\"H38\" s=\"8\"><f>(COUNTIF(H2:H33,&quot;Pass&quot;)/COUNTA(A2:A33))*100</f><v>100</v></c></row>\n");
        s1Xml.append("  </sheetData>\n</worksheet>");

        // --- 2. XML SHEET 2: CROSS-BROWSER & REGRESSION ---
        StringBuilder s2Xml = new StringBuilder();
        s2Xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
        s2Xml.append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">\n");
        s2Xml.append("  <cols>\n");
        s2Xml.append("    <col min=\"1\" max=\"1\" width=\"6\" customWidth=\"1\"/>\n");
        s2Xml.append("    <col min=\"2\" max=\"2\" width=\"35\" customWidth=\"1\"/>\n");
        s2Xml.append("    <col min=\"3\" max=\"3\" width=\"14\" customWidth=\"1\"/>\n");
        s2Xml.append("    <col min=\"4\" max=\"4\" width=\"16\" customWidth=\"1\"/>\n");
        s2Xml.append("    <col min=\"5\" max=\"5\" width=\"16\" customWidth=\"1\"/>\n");
        s2Xml.append("    <col min=\"6\" max=\"6\" width=\"16\" customWidth=\"1\"/>\n");
        s2Xml.append("    <col min=\"7\" max=\"7\" width=\"18\" customWidth=\"1\"/>\n");
        s2Xml.append("    <col min=\"8\" max=\"8\" width=\"16\" customWidth=\"1\"/>\n");
        s2Xml.append("    <col min=\"9\" max=\"9\" width=\"45\" customWidth=\"1\"/>\n");
        s2Xml.append("  </cols>\n");
        s2Xml.append("  <sheetData>\n");

        s2Xml.append("    <row r=\"1\">\n");
        for (int i = 0; i < h2.length; i++) {
            s2Xml.append("      <c r=\"").append(getColLetter(i)).append("1\" s=\"1\" t=\"inlineStr\"><is><t>")
                    .append(escapeXml(h2[i])).append("</t></is></c>\n");
        }
        s2Xml.append("    </row>\n");

        for (int r = 0; r < d2.length; r++) {
            int rowNum = r + 2;
            s2Xml.append("    <row r=\"").append(rowNum).append("\">\n");
            for (int c = 0; c < d2[r].length; c++) {
                String col = getColLetter(c);
                String val = d2[r][c];
                int styleId = 0;
                if (c >= 3 && c <= 7) {
                    if ("Pass".equalsIgnoreCase(val)) styleId = 5;
                    else if ("Fail".equalsIgnoreCase(val)) styleId = 6;
                    else if ("Minor".equalsIgnoreCase(val) || "Warning".equalsIgnoreCase(val)) styleId = 3;
                }
                s2Xml.append("      <c r=\"").append(col).append(rowNum).append("\" s=\"").append(styleId)
                        .append("\" t=\"inlineStr\"><is><t xml:space=\"preserve\">").append(escapeXml(val)).append("</t></is></c>\n");
            }
            s2Xml.append("    </row>\n");
        }
        s2Xml.append("  </sheetData>\n</worksheet>");

        // --- 3. XML SHEET 3: DEFECT LOG & BUG REPORT ---
        StringBuilder s3Xml = new StringBuilder();
        s3Xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
        s3Xml.append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">\n");
        s3Xml.append("  <cols>\n");
        s3Xml.append("    <col min=\"1\" max=\"1\" width=\"14\" customWidth=\"1\"/>\n"); // Bug ID
        s3Xml.append("    <col min=\"2\" max=\"2\" width=\"18\" customWidth=\"1\"/>\n"); // Modul
        s3Xml.append("    <col min=\"3\" max=\"3\" width=\"38\" customWidth=\"1\"/>\n"); // Judul Defect
        s3Xml.append("    <col min=\"4\" max=\"4\" width=\"14\" customWidth=\"1\"/>\n"); // Severity
        s3Xml.append("    <col min=\"5\" max=\"5\" width=\"10\" customWidth=\"1\"/>\n"); // Priority
        s3Xml.append("    <col min=\"6\" max=\"6\" width=\"25\" customWidth=\"1\"/>\n"); // Environment
        s3Xml.append("    <col min=\"7\" max=\"7\" width=\"45\" customWidth=\"1\"/>\n"); // Steps
        s3Xml.append("    <col min=\"8\" max=\"8\" width=\"36\" customWidth=\"1\"/>\n"); // Expected
        s3Xml.append("    <col min=\"9\" max=\"9\" width=\"36\" customWidth=\"1\"/>\n"); // Actual
        s3Xml.append("    <col min=\"10\" max=\"10\" width=\"14\" customWidth=\"1\"/>\n"); // Status
        s3Xml.append("  </cols>\n");
        s3Xml.append("  <sheetData>\n");

        s3Xml.append("    <row r=\"1\">\n");
        for (int i = 0; i < h3.length; i++) {
            s3Xml.append("      <c r=\"").append(getColLetter(i)).append("1\" s=\"1\" t=\"inlineStr\"><is><t>")
                    .append(escapeXml(h3[i])).append("</t></is></c>\n");
        }
        s3Xml.append("    </row>\n");

        for (int r = 0; r < d3.length; r++) {
            int rowNum = r + 2;
            s3Xml.append("    <row r=\"").append(rowNum).append("\">\n");
            for (int c = 0; c < d3[r].length; c++) {
                String col = getColLetter(c);
                String val = d3[r][c];
                int styleId = 0;
                if (c == 3) { // Severity
                    if ("Critical".equalsIgnoreCase(val)) styleId = 6; // Merah
                    else if ("High".equalsIgnoreCase(val)) styleId = 3; // Oranye
                    else if ("Medium".equalsIgnoreCase(val)) styleId = 4; // Cyan/Kuning
                    else if ("Low".equalsIgnoreCase(val)) styleId = 0;
                } else if (c == 9) { // Status Bug
                    if ("Resolved".equalsIgnoreCase(val)) styleId = 5; // Hijau
                    else if ("Open".equalsIgnoreCase(val)) styleId = 6; // Merah
                }
                s3Xml.append("      <c r=\"").append(col).append(rowNum).append("\" s=\"").append(styleId)
                        .append("\" t=\"inlineStr\"><is><t xml:space=\"preserve\">").append(escapeXml(val)).append("</t></is></c>\n");
            }
            s3Xml.append("    </row>\n");
        }
        s3Xml.append("  </sheetData>\n</worksheet>");

        // --- 4. STYLES XML ---
        String stylesXml =
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                        "<styleSheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">\n" +
                        "  <fonts count=\"8\">\n" +
                        "    <font><sz val=\"11\"/><name val=\"Calibri\"/></font>\n" +
                        "    <font><b/><sz val=\"11\"/><color rgb=\"FFFFFFFF\"/><name val=\"Calibri\"/></font>\n" +
                        "    <font><b/><sz val=\"11\"/><color rgb=\"FF155724\"/><name val=\"Calibri\"/></font>\n" +
                        "    <font><b/><sz val=\"11\"/><color rgb=\"FF856404\"/><name val=\"Calibri\"/></font>\n" +
                        "    <font><b/><sz val=\"11\"/><color rgb=\"FF0C5460\"/><name val=\"Calibri\"/></font>\n" +
                        "    <font><b/><sz val=\"11\"/><color rgb=\"FF721C24\"/><name val=\"Calibri\"/></font>\n" +
                        "    <font><b/><sz val=\"11\"/><color rgb=\"FF000000\"/><name val=\"Calibri\"/></font>\n" +
                        "    <font><u/><sz val=\"11\"/><color rgb=\"FF0563C1\"/><name val=\"Calibri\"/></font>\n" +
                        "  </fonts>\n" +
                        "  <fills count=\"7\">\n" +
                        "    <fill><patternFill patternType=\"none\"/></fill>\n" +
                        "    <fill><patternFill patternType=\"gray125\"/></fill>\n" +
                        "    <fill><patternFill patternType=\"solid\"><fgColor rgb=\"FF2F5597\"/></patternFill></fill>\n" +
                        "    <fill><patternFill patternType=\"solid\"><fgColor rgb=\"FFD4EDDA\"/></patternFill></fill>\n" +
                        "    <fill><patternFill patternType=\"solid\"><fgColor rgb=\"FFFFF3CD\"/></patternFill></fill>\n" +
                        "    <fill><patternFill patternType=\"solid\"><fgColor rgb=\"FFD1ECF1\"/></patternFill></fill>\n" +
                        "    <fill><patternFill patternType=\"solid\"><fgColor rgb=\"FFF8D7DA\"/></patternFill></fill>\n" +
                        "  </fills>\n" +
                        "  <borders count=\"1\"><border><left/><right/><top/><bottom/><diagonal/></border></borders>\n" +
                        "  <cellStyleXfs count=\"1\"><xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\"/></cellStyleXfs>\n" +
                        "  <cellXfs count=\"10\">\n" +
                        "    <xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\" xfId=\"0\" applyAlignment=\"1\"><alignment wrapText=\"1\" vertical=\"top\"/></xf>\n" +
                        "    <xf numFmtId=\"0\" fontId=\"1\" fillId=\"2\" borderId=\"0\" xfId=\"0\" applyFont=\"1\" applyFill=\"1\" applyAlignment=\"1\"><alignment horizontal=\"center\" vertical=\"center\"/></xf>\n" +
                        "    <xf numFmtId=\"0\" fontId=\"2\" fillId=\"3\" borderId=\"0\" xfId=\"0\" applyFont=\"1\" applyFill=\"1\" applyAlignment=\"1\"><alignment horizontal=\"center\" vertical=\"center\"/></xf>\n" +
                        "    <xf numFmtId=\"0\" fontId=\"3\" fillId=\"4\" borderId=\"0\" xfId=\"0\" applyFont=\"1\" applyFill=\"1\" applyAlignment=\"1\"><alignment horizontal=\"center\" vertical=\"center\"/></xf>\n" +
                        "    <xf numFmtId=\"0\" fontId=\"4\" fillId=\"5\" borderId=\"0\" xfId=\"0\" applyFont=\"1\" applyFill=\"1\" applyAlignment=\"1\"><alignment horizontal=\"center\" vertical=\"center\"/></xf>\n" +
                        "    <xf numFmtId=\"0\" fontId=\"2\" fillId=\"3\" borderId=\"0\" xfId=\"0\" applyFont=\"1\" applyFill=\"1\" applyAlignment=\"1\"><alignment horizontal=\"center\" vertical=\"center\"/></xf>\n" +
                        "    <xf numFmtId=\"0\" fontId=\"5\" fillId=\"6\" borderId=\"0\" xfId=\"0\" applyFont=\"1\" applyFill=\"1\" applyAlignment=\"1\"><alignment horizontal=\"center\" vertical=\"center\"/></xf>\n" +
                        "    <xf numFmtId=\"0\" fontId=\"6\" fillId=\"0\" borderId=\"0\" xfId=\"0\" applyFont=\"1\" applyAlignment=\"1\"><alignment horizontal=\"right\" vertical=\"center\"/></xf>\n" +
                        "    <xf numFmtId=\"0\" fontId=\"6\" fillId=\"0\" borderId=\"0\" xfId=\"0\" applyFont=\"1\" applyAlignment=\"1\"><alignment horizontal=\"center\" vertical=\"center\"/></xf>\n" +
                        "    <xf numFmtId=\"0\" fontId=\"7\" fillId=\"0\" borderId=\"0\" xfId=\"0\" applyFont=\"1\" applyAlignment=\"1\"><alignment vertical=\"top\"/></xf>\n" +
                        "  </cellXfs>\n" +
                        "  <cellStyles count=\"1\"><cellStyle name=\"Normal\" xfId=\"0\" builtinId=\"0\"/></cellStyles>\n" +
                        "</styleSheet>";

        // --- 5. BUNGKUS KE DALAM FILE OPENXML ZIP (.xlsx) 3-SHEET ---
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(fileName))) {
            addZipEntry(zos, "[Content_Types].xml",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                            "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">\n" +
                            "  <Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>\n" +
                            "  <Default Extension=\"xml\" ContentType=\"application/xml\"/>\n" +
                            "  <Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/>\n" +
                            "  <Override PartName=\"/xl/worksheets/sheet1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>\n" +
                            "  <Override PartName=\"/xl/worksheets/sheet2.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>\n" +
                            "  <Override PartName=\"/xl/worksheets/sheet3.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>\n" +
                            "  <Override PartName=\"/xl/styles.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml\"/>\n" +
                            "</Types>");

            addZipEntry(zos, "_rels/.rels",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                            "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">\n" +
                            "  <Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/>\n" +
                            "</Relationships>");

            addZipEntry(zos, "xl/workbook.xml",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                            "<workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">\n" +
                            "  <sheets>\n" +
                            "    <sheet name=\"Test Execution Results\" sheetId=\"1\" r:id=\"rId1\"/>\n" +
                            "    <sheet name=\"Cross-Browser &amp; Regression\" sheetId=\"2\" r:id=\"rId3\"/>\n" +
                            "    <sheet name=\"Defect Log &amp; Bug Report\" sheetId=\"3\" r:id=\"rId4\"/>\n" +
                            "  </sheets>\n" +
                            "</workbook>");

            addZipEntry(zos, "xl/_rels/workbook.xml.rels",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                            "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">\n" +
                            "  <Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet1.xml\"/>\n" +
                            "  <Relationship Id=\"rId2\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles\" Target=\"styles.xml\"/>\n" +
                            "  <Relationship Id=\"rId3\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet2.xml\"/>\n" +
                            "  <Relationship Id=\"rId4\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet3.xml\"/>\n" +
                            "</Relationships>");

            addZipEntry(zos, "xl/styles.xml", stylesXml);
            addZipEntry(zos, "xl/worksheets/sheet1.xml", s1Xml.toString());
            addZipEntry(zos, "xl/worksheets/sheet2.xml", s2Xml.toString());
            addZipEntry(zos, "xl/worksheets/sheet3.xml", s3Xml.toString());
        }
    }

    private static void addZipEntry(ZipOutputStream zos, String entryName, String content) throws IOException {
        ZipEntry entry = new ZipEntry(entryName);
        zos.putNextEntry(entry);
        zos.write(content.getBytes(StandardCharsets.UTF_8));
        zos.closeEntry();
    }

    private static String getColLetter(int colIndex) {
        return String.valueOf((char) ('A' + colIndex));
    }

    private static String escapeXml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;")
                .replace("\n", "&#10;");
    }
}