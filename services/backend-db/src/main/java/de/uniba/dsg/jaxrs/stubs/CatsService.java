package de.uniba.dsg.jaxrs.stubs;

import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import de.uniba.dsg.jaxrs.Configuration;
import de.uniba.dsg.jaxrs.models.Cat;
import de.uniba.dsg.jaxrs.models.CatDTO;

public class CatsService {

	private MongoCollection<CatDTO> catCollection;

	private static final Semaphore sem = new Semaphore(1);
	private static CatsService instance;

	private CatsService() {
		final Properties props = Configuration.loadProperties();
		final MongoCredential credentials = MongoCredential.createCredential(props.getProperty("DB_USER"), props.getProperty("DB_USER_TABLE"), props.getProperty("DB_PWD").toCharArray());
		final MongoClientOptions options = MongoClientOptions.builder().sslEnabled(false).build();
		final MongoClient client = new MongoClient(new ServerAddress(props.getProperty("SERVICE_NAME"), Integer.parseInt(props.getProperty("MONGO_DB_PORT"))), credentials, options);

		final CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
				fromProviders(PojoCodecProvider.builder().automatic(true).build()));

		final MongoDatabase database = client.getDatabase(props.getProperty("MONGO_DB_TABLE")).withCodecRegistry(pojoCodecRegistry);
		catCollection = database.getCollection(props.getProperty("MONGO_DB_COLLECTION"), CatDTO.class);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				client.close();
			}
		});
	}

	public static CatsService getInstance() {
		try {
			sem.acquireUninterruptibly();
			if (CatsService.instance == null) {
				CatsService.instance = new CatsService();
			}
		} finally {
			sem.release();
		}
		return CatsService.instance;
	}

	private List<Cat> convert(FindIterable<CatDTO> iterableCats) {
		List<Cat> cats = new ArrayList<>();
		Consumer<CatDTO> consumer = c -> cats.add(c.convert());
		iterableCats.forEach(consumer);
		return cats;
	}

	public List<Cat> getFamousCats() {
		return this.convert(catCollection.find());
	}

	public List<Cat> getFamousCats(String movie) {
		return this.convert(catCollection.find(eq("movie", movie)));
	}

	public Cat getFamousCat(int id) {
		List<Cat> cats = this.convert(catCollection.find(eq("identifier", id)));
		if (cats.size() == 0) {
			return null;
		}
		return cats.get(0);
	}

	public boolean deleteCat(int id) {
		long deleted = catCollection.deleteOne(eq("identifier", id)).getDeletedCount();
		if(deleted == 1) {
			return true;
		} else {
			return false;
		}
	}

	public Cat addCat(Cat newCat) {
		catCollection.insertOne(new CatDTO(newCat));
		return this.getFamousCat(newCat.id);
	}

	public Cat updateCat(int id, Cat updatedCat) {

		Document document = new Document();

		Optional.ofNullable(updatedCat.getName()).ifPresent(d -> document.append("name", d));
		Optional.ofNullable(updatedCat.getImageUrl()).ifPresent(d -> document.append("imageUrl", d));
		Optional.ofNullable(updatedCat.getMovie()).ifPresent(d -> document.append("movie", d));

		catCollection.updateOne(eq("identifier", id), new Document("$set", document));
		return this.getFamousCat(updatedCat.id);
	}

}
