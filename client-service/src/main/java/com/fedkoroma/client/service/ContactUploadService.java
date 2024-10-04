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
        String fileType = file.getContentType();

        if ("text/csv".equals(fileType)) {
            processCSVFile(file, emailOwner);
        } else if ("application/vnd.ms-excel".equals(fileType) || "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(fileType)) {
            processExcelFile(file, emailOwner);
        } else {
            throw new IllegalArgumentException("Unsupported file type");
        }
    }

    private void processCSVFile(MultipartFile file, String emailOwner) throws Exception {
        log.info("Started processing CSV file for user: " + emailOwner);

        if (file.isEmpty()) {
            log.error("Uploaded file is empty");
            throw new IllegalArgumentException("Uploaded file is empty");
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withDelimiter(';').withHeader())) {

            List<Contact> contacts = new ArrayList<>();
            for (CSVRecord record : csvParser) {
                String name = record.get("name");
                String phoneNumber = record.get("phone");
                String email = record.get("email");

                if (name == null || name.isEmpty() || phoneNumber == null || phoneNumber.isEmpty() || email == null || email.isEmpty()) {
                    log.error("Invalid contact data: name={}, phoneNumber={}, email={}", name, phoneNumber, email);
                    throw new IllegalArgumentException("Contact data is incomplete");
                }

                Contact contact = new Contact();
                contact.setName(name);
                contact.setPhoneNumber(phoneNumber);
                contact.setEmail(email);
                contacts.add(contact);
            }

            // Сохраняем контакты в базу данных (здесь ваш метод для сохранения)
            saveContacts(contacts, emailOwner);
        }catch (Exception e) {
            log.error("Error processing CSV file for user {}: {}", emailOwner, e.getMessage(), e);
            throw new Exception("Failed to upload and process file.", e);
        }
    }

    private void processExcelFile(MultipartFile file, String emailOwner) throws Exception {
        log.info("Started processing Excel file for user: " + emailOwner);

        if (file.isEmpty()) {
            log.error("Uploaded file is empty");
            throw new IllegalArgumentException("Uploaded file is empty");
        }

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            List<Contact> contacts = new ArrayList<>();

            for (Row row : sheet) {
                Cell nameCell = row.getCell(0);
                Cell phoneNumberCell = row.getCell(1);
                Cell emailCell = row.getCell(2);

                String name = nameCell != null ? nameCell.getStringCellValue() : "";
                String phoneNumber = phoneNumberCell != null ? phoneNumberCell.getStringCellValue() : "";
                String email = emailCell != null ? emailCell.getStringCellValue() : "";

                if (name.isEmpty() || phoneNumber.isEmpty() || email.isEmpty()) {
                    log.error("Invalid contact data: name={}, phoneNumber={}, email={}", name, phoneNumber, email);
                    throw new IllegalArgumentException("Contact data is incomplete");
                }

                Contact contact = new Contact();
                contact.setName(name);
                contact.setPhoneNumber(phoneNumber);
                contact.setEmail(email);
                contacts.add(contact);
            }

            // Сохраняем контакты в базу данных (здесь ваш метод для сохранения)
            saveContacts(contacts, emailOwner);
        }catch (Exception e) {
            log.error("Error processing Excel file for user {}: {}", emailOwner, e.getMessage(), e);
            throw new Exception("Failed to upload and process file.", e);
        }
    }

    private void saveContacts(List<Contact> contacts, String emailOwner) {
        // Логика для сохранения контактов в базе данных
        User owner = userRepository.findByEmail(emailOwner)
                .orElseThrow(() -> new EmailNotFoundException("User not found"));

        for (Contact contact : contacts){
            contact.setUser(owner);
            contactRepository.save(contact);
        }
    }
}
