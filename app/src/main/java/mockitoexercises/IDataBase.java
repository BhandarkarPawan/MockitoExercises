package mockitoexercises;

public interface IDataBase {

	void create(String key, int value);

	void update(String key, int newValue);

	int read(String key);

	void delete(String key);

}