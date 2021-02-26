package songlib.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import javafx.stage.Stage;

import objects.Song;
import comparator.SongComparator;

/**
 * @author      Sahej Bansal
 */

public class ListController {
	@FXML
	ListView<Song> songlist;
	
	@FXML
	TextField editname, editartist, editalbum, edityear;
	
	@FXML
	TextField addname, addartist, addalbum, addyear;
	
	private ObservableList<Song> obslist;
	
	public void start(Stage mainStage) {
		obslist = FXCollections.observableArrayList(readFromFile("src/songs.txt"));
		songlist.setItems(obslist);
		
		songlist.setCellFactory(new Callback<ListView<Song>, ListCell<Song>>(){

			@Override
			public ListCell<Song> call(ListView<Song> s) {
				
				ListCell<Song> cell = new ListCell<Song>() {
					
					@Override
					protected void updateItem(Song s, boolean bln) {
						super.updateItem(s, bln);
						if (s != null) {
							setText(s.getTitle() + " | " + s.getArtist());
						} else {
							setText(null);
						}
					}
					
				};
				
				return cell;
				
			}			
			
		});
		
		if (!obslist.isEmpty()) {
			songlist.getSelectionModel().select(0);
		}
		
		showSong();
		
		songlist
			.getSelectionModel()
			.selectedItemProperty()
			.addListener((obs, oldVal, newVal) -> 
				showSong());
		
		mainStage.setOnCloseRequest(event -> {
			
			PrintWriter writer;
			try {
				
				File file = new File("src/songs.txt");
				file.createNewFile();
				writer = new PrintWriter(file);
				
				for (Song s: obslist) {
					
					writer.println(s.getTitle());
					writer.println(s.getArtist());
					writer.println(s.getAlbum());
					writer.println(s.getYear());
					
				}
				
				writer.close();
				
			} catch (Exception e) {
				
				e.printStackTrace();
				
			}
			
		});
		
	}
	
	@FXML
	private void handleEdit(ActionEvent event) {
		if (obslist.isEmpty()) {
			showError("Nothing to edit");
			return;
		}
		
		if(editname.getText() == null || editname.getText() == "") {
			showError("No item selected");
			return;
		}
		
		editname.setEditable(true);
		editartist.setEditable(true);
		editalbum.setEditable(true);
		edityear.setEditable(true);		
		editname.setDisable(false);
		editartist.setDisable(false);
		editalbum.setDisable(false);
		edityear.setDisable(false);
	}
	
	@FXML
	private void handleSave(ActionEvent event) {
		if (editname.isEditable() != true) {
			showError("No changes made");
			return;
		}
		int index = songlist.getSelectionModel().getSelectedIndex();
		
		Dialog<Boolean> dialog = new Dialog<>();
		dialog.setTitle("Save changes");
		dialog.setContentText("Confirm save changes?");
		dialog.setResizable(false);
		
		ButtonType buttonTypeSave = new ButtonType("Save", ButtonData.OK_DONE);
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		
		dialog.getDialogPane().getButtonTypes().add(buttonTypeSave);
		dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
		
		dialog.setResultConverter(new Callback<ButtonType, Boolean>() {
			@Override
			public Boolean call(ButtonType b) {
				if (b == buttonTypeSave) {
					String error = checkFields(editname.getText(), editartist.getText(), 
							editalbum.getText(), edityear.getText());
					if (error != null) {
						if (error.equals("Title and Artist cannot already exist in library.") 
						   && editname.getText().equals(songlist.getSelectionModel().getSelectedItem().getTitle()) 
						   && editartist.getText().equals(songlist.getSelectionModel().getSelectedItem().getArtist()))
						   {
					   			return true;
						   }
						showError(error);
						return null;
						
					}
					return true;
					
				} else if (b == buttonTypeCancel) {
					return false;
				}		
				return null;
				
			}
		});
		
		Optional<Boolean> result = dialog.showAndWait();
		
		if (result.isPresent()) {
			if (result.get() == true) {
				Song temp = new Song(editname.getText().trim(),editartist.getText().trim(),
						editalbum.getText().trim(),edityear.getText().trim());
				   obslist.set(index,temp);
				   songlist.getSelectionModel().select(index);
				   showSong();
				   editname.setEditable(false);
				   editartist.setEditable(false);
				   editalbum.setEditable(false);
				   edityear.setEditable(false);
				   editname.setDisable(true);
				   editartist.setDisable(true);
				   editalbum.setDisable(true);
				   edityear.setDisable(true);
			} else if (result.get() == false) {
				//do nothing
			}
		}
		
	}
	
	@FXML
	private void handleAdd(ActionEvent event) {
		int index = songlist.getSelectionModel().getSelectedIndex();
		
		if (addname.getText() == null || addname.getText() == "") {
			showError("Cannot add song without Name");
			return;
		} else if (addartist.getText() == null || addartist.getText() == "") {
			showError("Cannot add song without Artist");
			return;
		}
		
		Dialog<Song> dialog = new Dialog<>();
		dialog.setTitle("Add new song");
		dialog.setContentText("Confirm add song?");
		dialog.setResizable(false);
		
		ButtonType buttonTypeAdd = new ButtonType("Add", ButtonData.OK_DONE);
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		
		dialog.getDialogPane().getButtonTypes().add(buttonTypeAdd);
		dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
		
		dialog.setResultConverter(new Callback<ButtonType, Song>() {
			@Override
			public Song call(ButtonType b) {
				if (b == buttonTypeAdd) {
					String error = checkFields(addname.getText(), addartist.getText(), 
							addalbum.getText(), addyear.getText());
					if (error != null) {
						showError(error);
						return null;
					}
					
					if(addyear.getText().trim().isEmpty() || addalbum.getText().trim().isEmpty()) {
						if(addyear.getText().trim().isEmpty() && addalbum.getText().trim().isEmpty()) {
							return new Song(addname.getText().trim(), 
									addartist.getText().trim(), 
									"", "");
							
						} else if (addyear.getText().trim().isEmpty()) {
							return new Song(addname.getText().trim(), 
									addartist.getText().trim(), 
									addalbum.getText().trim(), "");
							
						} else if (addalbum.getText().trim().isEmpty()) {
							return new Song(addname.getText().trim(), 
									addartist.getText().trim(), 
									"", addyear.getText().trim());
							
						}
					} else {
						return new Song(addname.getText().trim(), 
								addartist.getText().trim(), 
								addalbum.getText().trim(), addyear.getText().trim());
					}
					
				} else if (b == buttonTypeCancel) {
					return null;
					
				}		
				return null;
				
			}
		});
		
		Optional<Song> result = dialog.showAndWait();
		
		if (result.isPresent()) {
			if (result.get() != null) {
				Song temp = (Song) result.get();
				   obslist.add(temp);
				   FXCollections.sort(obslist, new SongComparator());
				   
				   if (obslist.size() == 1) {
					   songlist.getSelectionModel().select(0);
				   } else {
					   index = 0;
					   for (Song s : obslist) {
						   if (s == temp) {
							   songlist.getSelectionModel().select(index);
							   break;
						   }
						   index++;
					   }
				   }
				   editname.setEditable(false);
				   editartist.setEditable(false);
				   editalbum.setEditable(false);
				   edityear.setEditable(false);
				   editname.setDisable(true);
				   editartist.setDisable(true);
				   editalbum.setDisable(true);
				   edityear.setDisable(true);
			} else if (result.get() == null) {
				//do nothing
			}
		}
		
	}
	
	@FXML
	private void handleDelete(ActionEvent event) {
		if (obslist.isEmpty()) {
			showError("There is nothing to delete");
			return;
		}
		
		Song item = songlist.getSelectionModel().getSelectedItem();
		int index = songlist.getSelectionModel().getSelectedIndex();
		
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Delete Song");
		alert.setHeaderText("This change cannot be undone!");
		String content = "Confirm delete of " + item.getTitle() + "?"
				+ "\nClose this window from the top right to cancel delete action";
		alert.setContentText(content);
		
		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent()) {
			obslist.remove(item);
			
			if (obslist.isEmpty()) {
				editname.setText("");
				editartist.setText("");
				editalbum.setText("");
				edityear.setText("");
			} else if (index == obslist.size()-1) {
				songlist.getSelectionModel().select(index--);
			} else {
				songlist.getSelectionModel().select(index++);
			}
			showSong();
		}
	}
	
	private void showSong() {
	   if (songlist.getSelectionModel().getSelectedIndex() < 0)
		   return;
	   
	   Song s = songlist.getSelectionModel().getSelectedItem();
	   
	   editname.setText(s.getTitle());
	   editartist.setText(s.getArtist());
	   editalbum.setText(s.getAlbum());
	   edityear.setText(s.getYear());
   }
	
	private ArrayList<Song> readFromFile(String filePathName) {
	   ArrayList <Song> songs = new ArrayList<Song>();
	   BufferedReader br;
	   Path filePath = Paths.get(filePathName);
	   try {

			if (!new File(filePathName).exists())
			{
			   return songs;
			}
		   br = Files.newBufferedReader(filePath);
		   String line = br.readLine();
			
		   while (line != null) { 
	              
			   String title = line;
	               
			   line = br.readLine();
			   String artist = line;
	               
			   line = br.readLine();
			   String album = line;
	               
			   line = br.readLine();
			   String year = line;
	               
			   Song temp = new Song(title, artist, album, year);
			   songs.add(temp);
	               
			   line = br.readLine(); //to the next song name, if not null
		   }
		  
			
	   } catch (IOException e) {
		   e.printStackTrace();
	   }
	      
	   //sort songs alphabetically by title
	   Collections.sort(songs, new SongComparator());
	   return songs;
   }
	
	private void showError(String error) {
		   Alert alert = new Alert(AlertType.INFORMATION);
		   alert.setTitle("Error");
		   alert.setHeaderText("Error");
		   String content = error;
		   alert.setContentText(content);
		   alert.showAndWait();
	}
	
	private String checkFields(String title, String artist, String album, String year) {
	    if (title.trim().isEmpty())
		    return "Title cannot be empty.";
	    else if (artist.trim().isEmpty())
		    return "Artist cannot be empty.";
	    else if (!isUniqueSong(title, artist))
		    return "Title and Artist cannot already exist in library.";
	   
	    else if (!year.trim().isEmpty()) {
		    if (!year.trim().matches("[0-9]+"))
			   return "Year must only contain numbers.";
		    else if (year.trim().length() != 4)
			    return "Year must be 4 digits long.";
	    }
	   
	    return null;
    }
	
	private boolean isUniqueSong(String title, String artist) {
		for (Song s : obslist) {
			if (s.getTitle().toLowerCase().equals(title.trim().toLowerCase()) 
					&& s.getArtist().toLowerCase().equals(artist.trim().toLowerCase()))
			{
				return false;
			}
		}
		
		return true;
	}
	
}
