package com.fedkoroma.client.service;

import com.fedkoroma.client.exception.EmailNotFoundException;
import com.fedkoroma.client.model.Contact;
import com.fedkoroma.client.model.User;
import com.fedkoroma.client.repository.ContactRepository;
import com.fedkoroma.client.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactUploadService {

    private final ContactRepository contactRepository;
    private final UserRepository userRepository;

    public void processFile(MultipartFile file, String emailOwner) throws Exception {
        if (file.isEmpty()) {
            log.error("Uploaded file is empty");
            throw new IllegalArgumentException("Uploaded file is empty");
        }

        String fileType = file.getContentType();

        if ("text/csv".equals(fileType)) {
            processCSVFile(file, emailOwner);
        } else if (isExcelFile(fileType)) {
            processExcelFile(file, emailOwner);
        } else {
            throw new IllegalArgumentException("Unsupported file type");
        }
    }

    private boolean isExcelFile(String fileType) {
        return "application/vnd.ms-excel".equals(fileType) ||
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(fileType);
    }

    private void processCSVFile(MultipartFile file, String emailOwner) throws Exception {
        log.info("Started processing CSV file for user: {}", emailOwner);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withDelimiter(';').withHeader())) {

            List<Contact> contacts = new ArrayList<>();
            for (CSVRecord record : csvParser) {
                addContactFromRecord(contacts, record.get("name"), record.get("phone"), record.get("email"));
            }
            saveContacts(contacts, emailOwner);

        } catch (IOException e) {
            log.error("Error processing CSV file for user {}: {}", emailOwner, e.getMessage(), e);
            throw new Exception("Failed to upload and process file.", e);
        }
    }

    private void processExcelFile(MultipartFile file, String emailOwner) throws Exception {
        log.info("Started processing Excel file for user: {}", emailOwner);

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            List<Contact> contacts = new ArrayList<>();

            for (Row row : sheet) {
                addContactFromRecord(contacts,
                        getCellValueAsString(row.getCell(0)),
                        getCellValueAsString(row.getCell(1)),
                        getCellValueAsString(row.getCell(2)));
            }
            saveContacts(contacts, emailOwner);

        } catch (Exception e) {
            log.error("Error processing Excel file for user {}: {}", emailOwner, e.getMessage(), e);
            throw new Exception("Failed to upload and process file.", e);
        }
    }

    private void addContactFromRecord(List<Contact> contacts, String name, String phone, String email) {
        if (isInvalid(name, phone, email)) {
            log.warn("Invalid contact data: name={}, phone={}, email={}", name, phone, email);
            return;
        }

        Contact contact = new Contact();
        contact.setName(name);
        contact.setPhoneNumber(phone);
        contact.setEmail(email);
        contacts.add(contact);
    }

    private boolean isInvalid(String name, String phone, String email) {
        return name == null || name.isEmpty() ||
                phone == null || phone.isEmpty() ||
                email == null || email.isEmpty();
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC: return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            default: return "";
        }
    }

    private void saveContacts(List<Contact> contacts, String emailOwner) {
        if (contacts.isEmpty()) {
            log.warn("No valid contacts to save for user {}", emailOwner);
            return;
        }

        User owner = userRepository.findByEmail(emailOwner)
                .orElseThrow(() -> new EmailNotFoundException("User not found"));

        contacts.forEach(contact -> {
            contact.setUser(owner);
            contactRepository.save(contact);
        });
    }
}
