package mockitoexercises;

import java.util.UUID;

public class MyPStack {

	private int maxSize = 10;
	private int[] stackArray;
	private int top;

	private IDataBase dataBase;
	private String id = UUID.randomUUID().toString();

	public MyPStack() {
		stackArray = new int[maxSize];
		top = -1;
	}

	public String getId() {
		return id;
	}

	public MyPStack(int capacity) {
		maxSize = capacity;
		stackArray = new int[maxSize];
		top = -1;
	}

	public MyPStack(IDataBase db) {
		dataBase = db;
		stackArray = new int[maxSize];
		top = -1;
	}

	public void push(int j) throws OverflowException {
		if (isFull())
			throw new OverflowException();

		if (dataBase != null)
			if (top == -1)
				dataBase.create(id, j);
			else
				dataBase.update(id, j);

		stackArray[++top] = j;
	}

	public int pop() throws InvalidOperationException {
		if (isEmpty())
			throw new InvalidOperationException();

		if (dataBase != null) {
			if (top == 0)
				dataBase.delete(id);
			else {
				dataBase.update(id, stackArray[top - 1]);
			}
		}
		return stackArray[top--];
	}

	public int peek() throws InvalidOperationException {
		if (isEmpty())
			throw new InvalidOperationException();
		return stackArray[top];
	}

	public int size() {
		return top + 1;
	}

	public boolean isEmpty() {
		return (top == -1);
	}

	public boolean isFull() {
		return (top == maxSize - 1);
	}

	public int maxSize() { // added for visibility to test overflow
		return maxSize;
	}

	public void reset() {
		if (dataBase != null && top >= 0) {
			int lastTop = dataBase.read(id);

			stackArray = new int[maxSize];
			stackArray[0] = lastTop;
			top = 0;
		}
	}
}