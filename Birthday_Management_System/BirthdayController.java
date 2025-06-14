package Birthday_Management_System;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.LocalDate;

public class BirthdayController {
    private VBox view;
    private ListView<String> listView;
    private ObservableList<String> listItems;

    private TextField nameField, searchField;
    private DatePicker datePicker;

    private BirthdayDAO dao = new BirthdayDAO();

    public BirthdayController() {
        // Root layout
        view = new VBox(10);
        view.setPadding(new Insets(10));

        // Title
        Label title = new Label("জন্মদিন তালিকা");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // List of birthdays
        listItems = FXCollections.observableArrayList();
        listView = new ListView<>(listItems);
        refreshList(); // Load initial data

        // Form for adding a birthday
        nameField = new TextField();
        nameField.setPromptText("নাম");

        datePicker = new DatePicker();

        Button addBtn = new Button("যোগ করুন");
        addBtn.setOnAction(e -> addBirthday());

        HBox form = new HBox(10, nameField, datePicker, addBtn);

        // Delete button
        Button deleteBtn = new Button("মুছে ফেলুন");
        deleteBtn.setOnAction(e -> deleteSelected());

        // Search functionality
        searchField = new TextField();
        searchField.setPromptText("নাম বা মাস দিয়ে খুঁজুন");

        Button searchBtn = new Button("খুঁজুন");
        searchBtn.setOnAction(e -> searchBirthdays());

        // Today's birthdays
        Label todayLabel = new Label("আজকের জন্মদিন:");
        ListView<String> todayList = new ListView<>();
        ObservableList<String> todayItems = FXCollections.observableArrayList();
        for (Birthday b : dao.getTodaysBirthdays()) {
            todayItems.add(b.getName() + " - শুভ জন্মদিন!");
        }
        todayList.setItems(todayItems);

        // Add everything to the layout
        view.getChildren().addAll(title, form, listView, deleteBtn, searchField, searchBtn, todayLabel, todayList);
    }

    public VBox getView() {
        return view;
    }

    // Helper methods

    private void refreshList() {
        listItems.clear();
        for (Birthday b : dao.getAllBirthdays()) {
            listItems.add(b.getName() + " - " + b.getBirthdate());
        }
    }

    private void addBirthday() {
        String name = nameField.getText();
        LocalDate date = datePicker.getValue();

        if (name.isEmpty() || date == null) return;

        dao.addBirthday(new Birthday(name, date));
        refreshList();
        nameField.clear();
        datePicker.setValue(null);
    }

    private void deleteSelected() {
        int index = listView.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            Birthday b = dao.getAllBirthdays().get(index);
            dao.deleteBirthday(b.getId());
            refreshList();
        }
    }

    private void searchBirthdays() {
        String query = searchField.getText().trim();
        listItems.clear();
        for (Birthday b : dao.searchByNameOrMonth(query)) {
            listItems.add(b.getName() + " - " + b.getBirthdate());
        }
    }
}
