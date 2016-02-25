import java.util.*;
import java.io.*;
import java.util.logging.*;

public class skiplistv2Wrapped<Key, Value> implements myListInterface<Key, Value> {
	private myListInterface<Key, Value> proxied;
	private static Logger logger;
	private static FileHandler fileHandler;

	static {
		logger = Logger.getLogger(skiplistv2.class.getName());
		try {
			fileHandler = new FileHandler("log.log", false);
		} catch (SecurityException exception) {
			exception.printStackTrace();
		} catch (IOException exception) {
			exception.printStackTrace();
		}

		fileHandler.setFormatter(new SimpleFormatter());
		logger.addHandler(fileHandler);
		logger.setLevel(Level.CONFIG);
	}

	public skiplistv2Wrapped(myListInterface<Key, Value> proxied) {
		this.proxied = proxied;
	}

	public Value remove(Key arg0) throws RuntimeException {
		Value result;
		try {
			logger.log(Level.INFO, "remove");
			long startT = System.nanoTime();
			result =  proxied.remove(arg0);
			long endT = System.nanoTime();
			long executionT = endT - startT;
			logger.log(Level.INFO, executionT + " ns");
		} catch (Exception exception) {
			exception.printStackTrace();
			throw new RuntimeException("Niespodziewany wyjatek: " + exception.getMessage());
		}

		return result;
	}

	public Value get(Key arg0) throws RuntimeException {
		Value result;
		try {
			logger.log(Level.INFO, "get");
			long startT = System.nanoTime();
			result =  proxied.get(arg0);
			long endT = System.nanoTime();
			long executionT = endT - startT;
			logger.log(Level.INFO, executionT + " ns");
		} catch (Exception exception) {
			exception.printStackTrace();
			throw new RuntimeException("Niespodziewany wyjatek: " + exception.getMessage());
		}

		return result;
	}

	public Value put(Key arg0, Value arg1) throws RuntimeException {
		Value result;
		try {
			logger.log(Level.INFO, "put");
			long startT = System.nanoTime();
			result =  proxied.put(arg0, arg1);
			long endT = System.nanoTime();
			long executionT = endT - startT;
			logger.log(Level.INFO, executionT + " ns");
		} catch (Exception exception) {
			exception.printStackTrace();
			throw new RuntimeException("Niespodziewany wyjatek: " + exception.getMessage());
		}

		return result;
	}

	public String toString() throws RuntimeException {
		String result;
		try {
			logger.log(Level.INFO, "toString");
			long startT = System.nanoTime();
			result =  proxied.toString();
			long endT = System.nanoTime();
			long executionT = endT - startT;
			logger.log(Level.INFO, executionT + " ns");
		} catch (Exception exception) {
			exception.printStackTrace();
			throw new RuntimeException("Niespodziewany wyjatek: " + exception.getMessage());
		}

		return result;
	}

	public Integer size() throws RuntimeException {
		Integer result;
		try {
			logger.log(Level.INFO, "size");
			long startT = System.nanoTime();
			result =  proxied.size();
			long endT = System.nanoTime();
			long executionT = endT - startT;
			logger.log(Level.INFO, executionT + " ns");
		} catch (Exception exception) {
			exception.printStackTrace();
			throw new RuntimeException("Niespodziewany wyjatek: " + exception.getMessage());
		}

		return result;
	}

	public boolean containsKey(Key arg0) throws RuntimeException {
		boolean result;
		try {
			logger.log(Level.INFO, "containsKey");
			long startT = System.nanoTime();
			result =  proxied.containsKey(arg0);
			long endT = System.nanoTime();
			long executionT = endT - startT;
			logger.log(Level.INFO, executionT + " ns");
		} catch (Exception exception) {
			exception.printStackTrace();
			throw new RuntimeException("Niespodziewany wyjatek: " + exception.getMessage());
		}

		return result;
	}

	public Key higherKey(Key arg0) throws RuntimeException {
		Key result;
		try {
			logger.log(Level.INFO, "higherKey");
			long startT = System.nanoTime();
			result =  proxied.higherKey(arg0);
			long endT = System.nanoTime();
			long executionT = endT - startT;
			logger.log(Level.INFO, executionT + " ns");
		} catch (Exception exception) {
			exception.printStackTrace();
			throw new RuntimeException("Niespodziewany wyjatek: " + exception.getMessage());
		}

		return result;
	}

	public Key lowerKey(Key arg0) throws RuntimeException {
		Key result;
		try {
			logger.log(Level.INFO, "lowerKey");
			long startT = System.nanoTime();
			result =  proxied.lowerKey(arg0);
			long endT = System.nanoTime();
			long executionT = endT - startT;
			logger.log(Level.INFO, executionT + " ns");
		} catch (Exception exception) {
			exception.printStackTrace();
			throw new RuntimeException("Niespodziewany wyjatek: " + exception.getMessage());
		}

		return result;
	}
}