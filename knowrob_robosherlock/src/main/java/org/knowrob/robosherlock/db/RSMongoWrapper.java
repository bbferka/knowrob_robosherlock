package org.knowrob.robosherlock.db;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.QueryBuilder;

import org.bson.BSONObject;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
//import org.opencv.core.*;
//import org.opencv.highgui.Highgui;


public class RSMongoWrapper {


	private MongoClient mongoClient;
	public DB db;
	public HashMap<String, DBCollection> collections;
	public Scene scene;

	public RSMongoWrapper(String db_name)
	{
		String host = "localhost";
		int port = 27017;
		try {
			mongoClient = new MongoClient(host, port);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			System.out.println("EXCEPTION!!!");
			e.printStackTrace();
		}
		System.out.println("Connected to mongoDB");
		db = mongoClient.getDB(db_name);
		//get all collections
		collections = new HashMap<String, DBCollection>();
		collections.put("cas", db.getCollection("cas"));
		collections.put("scene", db.getCollection("scene"));
		collections.put("depth_image_as_int", db.getCollection("depth_image_as_int"));
		collections.put("rgb_image_hires", db.getCollection("rgb_image_hires"));
		collections.put("camera_info_hires", db.getCollection("camera_info_hires"));
	}

	public DBObject getDocument(String ts,String collectionName)
	{
		DBCollection casColl = (DBCollection)collections.get("cas");
		System.out.println("Docuemnts in CAS collection:"  + casColl.count());
		Long timestamp =null;
		try{
			timestamp = Long.parseLong(ts);
		}
		catch(NumberFormatException e){
			System.out.println(e.getMessage());
		}
		BasicDBObject query = new BasicDBObject("_timestamp",timestamp);
		DBObject casD = casColl.findOne(query);
		if(casD == null){
			System.out.println("No entry found with that timestamp");
			return null;
		}
		else{
			if(collectionName.equals("cas")){
				return casD;
			}
			else{
				ObjectId sceneID = (ObjectId)casD.get(collectionName);
				query.clear();
				query.put("_id", sceneID);
				DBCollection coll = (DBCollection)collections.get(collectionName);
				DBObject doc = coll.findOne(query);	
				return doc;
			}
		}
	}

	public Scene getScene(String ts)
	{
		DBObject sceneD = getDocument(ts,"scene");
		if(sceneD!=null)
		{
			return new Scene(sceneD,ts);
		}
		else return null;
	}
	public void getRGB(String ts)
	{
		DBObject rgbD = getDocument(ts,"rgb_image_hires");
		//convert to something useful.. cv.Mat?
//		convertToCvMat(rgbD);
		//advertise result on the topic for open-ease
	}
	public void getDepth(String ts)
	{
		DBObject depthD = getDocument(ts,"depth_image_as_int");
		//convert to something useful.. cv.Mat?
//		convertToCvMat(depthD);
	}
	public void getCamInfo(String ts)
	{
		DBObject camInfoD = getDocument(ts,"camera_info_hires");
		//maybe we don't even need this
	}
//
//	public Mat convertToCvMat(DBObject imgObj)
//	{
//		Integer cols = (Integer)imgObj.get("cols");
//		Integer rows = (Integer)imgObj.get("rows");
//		Integer mat_type = (Integer)imgObj.get("mat_type");
//		byte[] myBytes = (byte []) imgObj.get("data");
//		System.out.println("Cols: "+ cols + " Rows: " + rows + " Mat Type: "+mat_type);
//		Mat img = null;
//		if (mat_type == 2)
//			img= new Mat(rows, cols,CvType.CV_8U);
//		else
//			img=new Mat(rows,cols,mat_type);
//		if(img.rows() !=0 && img.cols() !=0){
//			img.put(0, 0, myBytes);
//		}
//		return img;
//	}

}


