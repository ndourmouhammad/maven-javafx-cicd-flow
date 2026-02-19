package sn.mouhammad.isi.candidats;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import sn.mouhammad.isi.candidats.entity.Candidat;
import sn.mouhammad.isi.candidats.repository.implement.CandidatRepository;

import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    @FXML
    private TableView<Candidat> candidatTable;

    @FXML
    private TableColumn<Candidat, Integer> idColumn;

    @FXML
    private TableColumn<Candidat, String> nameColumn;

    @FXML
    private TableColumn<Candidat, String> niveauColumn;

    @FXML
    private TextField searchField;

    private CandidatRepository repository = new CandidatRepository();
    private ObservableList<Candidat> candidatList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        niveauColumn.setCellValueFactory(cellData -> {
            Integer niveau = cellData.getValue().getNiveau();
            String label = "Inconnu";
            if (niveau != null) {
                if (niveau < 20) {
                    label = "Junior";
                } else if (niveau <= 50) {
                    label = "Senior";
                } else {
                    label = "Expert";
                }
            }
            return new SimpleStringProperty(label);
        });

        loadCandidats();
    }

    private void loadCandidats() {
        candidatList.clear();
        candidatList.addAll(repository.findAll());
        candidatTable.setItems(candidatList);
    }

    @FXML
    protected void onSearch() {
        String mot = searchField.getText();
        candidatList.clear();
        if (mot == null || mot.isEmpty()) {
            candidatList.addAll(repository.findAll());
        } else {
            candidatList.addAll(repository.searchByAll(mot));
        }
        candidatTable.setItems(candidatList);
    }
}
