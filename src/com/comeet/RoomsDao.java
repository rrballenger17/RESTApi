
package com.comeet;  



import java.io.File; 
import java.io.FileInputStream; 
import java.io.FileNotFoundException;  
import java.io.FileOutputStream; 
import java.io.IOException; 
import java.io.ObjectInputStream; 
import java.io.ObjectOutputStream; 
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.comeet.data.*;

import microsoft.exchange.webservices.data.*;
import microsoft.exchange.webservices.data.autodiscover.IAutodiscoverRedirectionUrl;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.exception.service.local.ServiceLocalException;
import microsoft.exchange.webservices.data.core.service.folder.CalendarFolder;
import microsoft.exchange.webservices.data.core.service.item.Appointment;
import microsoft.exchange.webservices.data.core.service.schema.AppointmentSchema;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.misc.ITraceListener;
import microsoft.exchange.webservices.data.property.complex.EmailAddress;
import microsoft.exchange.webservices.data.property.complex.EmailAddressCollection;
import microsoft.exchange.webservices.data.search.CalendarView;
import microsoft.exchange.webservices.data.search.FindItemsResults;

import java.net.URI;



public class RoomsDao { 
	
	/*
	static class RedirectionUrlCallback implements IAutodiscoverRedirectionUrl {
	    public boolean autodiscoverRedirectionUrlValidationCallback(
	            String redirectionUrl) {
	        return redirectionUrl.toLowerCase().startsWith("https://");
	    }
	}*/

	

	

	
	   public List<EmailAddress> getRoomsList() throws Exception{ 



		   ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);
		   ExchangeCredentials credentials = new WebCredentials("jablack@meetl.ink", "Comeet599");
		   service.setCredentials(credentials);
		   service.setUrl(new URI("https://outlook.office365.com/EWS/Exchange.asmx"));

		   
		   List<EmailAddress> names = new ArrayList<EmailAddress>();
		   
		   EmailAddressCollection c = service.getRoomLists();
		   for (EmailAddress e: c){
			   
			   Collection<EmailAddress> rooms = service.getRooms(e);
			   
			   for(EmailAddress r: rooms){
				   System.out.println(r.toString());
				   System.out.println(r.getAddress());
				   System.out.println(r.getName());
				   names.add(r);
			   }
			   
		   }

		      return names; 
	   } 
	
	
	
	
   public List<Room> getAllRooms(){ 
	   List<EmailAddress> rooms = null;
	   try {
		 rooms = getRoomsList();
	} catch (Exception e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
      
      List<Room> roomList = null; 
      try { 
         File file = new File("Roo.dat"); 
         
         file.delete();
         
         if (!file.exists()) { 
        	
        	roomList = new ArrayList<Room>(); 
        	 
 
        	for(EmailAddress s: rooms){
        		Room room = new Room();
        		room.setName(s.getName());
        		room.setEmail(s.getAddress());
        		retrieveMetadata(room);
        		roomList.add(room);
        	}
        	
            //User user = new User(1, "Peter", "Teacher"); 
            
           // userList.add(user); 
            saveRoomList(roomList); 
         } 
         else{ 
            FileInputStream fis = new FileInputStream(file); 
            ObjectInputStream ois = new ObjectInputStream(fis); 
            roomList = (List<Room>) ois.readObject(); 
            ois.close(); 
         } 
      } catch (IOException e) { 
         e.printStackTrace(); 
      } catch (ClassNotFoundException e) { 
         e.printStackTrace(); 
      }   
      return roomList; 
   } 
   private void saveRoomList(List<Room> roomList){ 
      try { 
         File file = new File("Roo.dat"); 
         FileOutputStream fos;  
         fos = new FileOutputStream(file); 
         ObjectOutputStream oos = new ObjectOutputStream(fos); 
         oos.writeObject(roomList); 
         oos.close(); 
      } catch (FileNotFoundException e) { 
         e.printStackTrace(); 
      } catch (IOException e) { 
         e.printStackTrace(); 
      } 
   }
   
   private void retrieveMetadata(Room room)
   {
	   DataRepository db = new DataRepository();

	   Room metadata = db.retrieveRoomMetadata(room.getEmail());
	   
	   if(metadata != null)
	   {
		   room.setCapacity(metadata.getCapacity());
		   room.setCountry(metadata.getCountry());
		   room.setBuilding(metadata.getBuilding());
		   room.setNavigationMap(metadata.getNavigationMap());
		   room.setLatitude(metadata.getLatitude());
		   room.setCapacity(metadata.getCapacity());
		   room.setLongitude(metadata.getLongitude());
		   room.setMetroarea(metadata.getMetroarea());
		   room.setState(metadata.getState());
		   room.setRoomPic(metadata.getRoomPic());
	   }
	   
   }
}