# TixSplit - End-to-End QA Automation Framework

<p align="center">
  <img src="https://img.shields.io/github/actions/workflow/status/raihannadhif901-commits/tixsplit-qa-automation/ci.yml?branch=main&label=CI%20Pipeline&logo=githubactions&logoColor=white&style=for-the-badge" alt="CI Status"/>
  <img src="https://img.shields.io/badge/Tests-32%20Passed%20(100%25)-success?style=for-the-badge&logo=checkmarx" alt="Tests Passed"/>
  <img src="https://img.shields.io/badge/Java-17%20LTS-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java"/>
  <img src="https://img.shields.io/badge/Selenium-4.18.1-43B02A?style=for-the-badge&logo=selenium&logoColor=white" alt="Selenium"/>
  <img src="https://img.shields.io/badge/REST_Assured-5.4.0-007396?style=for-the-badge&logo=apache" alt="REST Assured"/>
  <img src="https://img.shields.io/badge/TestNG-7.9.0-FF7F00?style=for-the-badge" alt="TestNG"/>
</p>

---

##  Executive Summary & Case Study

**TixSplit** adalah platform terintegrasi untuk manajemen event komunitas, pemesanan tiket berbayar (dengan kode unik 001–999 dan biaya admin 5%), serta sistem pembagian tagihan otomatis (*Split Bill* multi-peserta).

Sebagai **QA Automation Engineer**, proyek ini bertujuan untuk menjamin stabilitas fungsional, akurasi transaksi keuangan hingga rupiah terakhir, serta mencegah terjadinya *overselling* kuota tiket di bawah lonjakan transaksi bersamaan (*concurrency*).

### Key Engineering Highlights
* **32 Automated Test Cases:** 20 API Tests (REST Assured) + 12 UI E2E Tests (Selenium POM).
* **100% Regression Pass Rate:** Menjamin zero side-effect regresi setelah data reset via seeder.
* **6 Defects Uncovered:** Berhasil menemukan dan mendokumentasikan 1 Critical bug (Race condition kuota), 2 High, 2 Medium, dan 1 Low bug.
* **CI/CD Integration:** Terintegrasi penuh dengan GitHub Actions menggunakan *headless browser execution* pada Ubuntu runner.
* **Autonomous Excel Reporting:** Script generator kustom Java (`GenerateExcelReport`) yang otomatis memproduksi laporan 3-Tab format `.xlsx` resmi beserta formula Excel.

---

## Testing Metrics Dashboard

| Metric | Result |
| :--- | :--- |
| **Total Automated Test Cases** | **32 Skenario (100% PASS)** |
| **API Coverage (REST Assured)** | 20 Endpoints (AUTH, Split Bill, Event, Community, Notif, Booking) |
| **UI Coverage (Selenium POM)** | 12 Skenario (Auth, Form Validation, Event Catalog, Community, DDT) |
| **Cross-Platform Compatibility** | Google Chrome, Mozilla Firefox, Microsoft Edge, Mobile 360px |
| **Execution Time (Local Suite)** | ~25 detik (API + UI Browser) |
| **CI/CD Execution Time** | ~1 menit 40 detik di GitHub Actions |

---

## Test Scope & Module Traceability

| Module | PRD Req ID | Automated Coverage | Focus Area & Logic |
| :--- | :--- | :---: | :--- |
| **Authentication** | `AUTH-1...5` | 5 API / 5 UI | Registrasi, Login JWT, Data-Driven Testing (DDT), Negative Validation |
| **Split Bill** | `SB-1...17` | 4 API / 2 UI | Kalkulasi proporsional, pembulatan floor + Rp1 leftover, JSON Schema |
| **Event & Catalog**| `EVT-1...8` | 4 API / 3 UI | Publikasi event, query parameter search, pagination, detail navigation |
| **Community** | `COM-1...4` | 3 API / 2 UI | Katalog komunitas, proteksi pembuatan rute bagi Guest |
| **Notification** | `NTF-1...3` | 2 API | Proteksi akses privat notifikasi lonceng dan error handling |
| **Booking & Pay** | `PAY-1...10` | 2 API | Checkout authorization, seat hold 15 menit, validasi pembayaran |

---

## Tech Stack & Architecture

```text
├── Languages & Frameworks  : Java 17 LTS, TestNG 7.9.0
├── UI Automation           : Selenium WebDriver 4.18.1 (Page Object Model)
├── API Automation          : REST Assured 5.4.0, Hamcrest Matchers, JSON Schema Validator
├── Build & Dependencies    : Apache Maven
├── CI/CD Pipeline          : GitHub Actions (Ubuntu-latest)
├── Reporting & Artifacts   : OpenXML (.xlsx 3-Tab Generator), TestNG HTML Report
└── Design Patterns Applied : Page Object Model (POM), Data-Driven Testing (DDT), Listener Pattern

```

## Author & Connect 

* **Nama**: Raihan Nadhif
* **Role**: QA Automation Engineer
* **LinkedIn**:[ linkedin.com/in/username-kamu](https://www.linkedin.com/in/raihan-nadhif-9b4473268/?lipi=urn%3Ali%3Apage%3Ad_flagship3_notifications%3BTXHe1S1jTuWcj7PlkGjz%2BA%3D%3D)
* **Program**: Dibimbing QA Engineering Bootcamp Final Project


