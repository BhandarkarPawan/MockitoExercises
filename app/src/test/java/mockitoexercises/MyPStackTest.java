/* 
 We strongly suggest that you use a dependency management tool 
 such as Maven or Gradle to add Hamcrest and Mockito dependencies
*/

package mockitoexercises;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertFalse;
/* these import statements are for latest versions of Hamcrest */
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

public class MyPStackTest {

	MyPStack s;
	IDataBase db;

	@Before
	public void setUp() throws Exception {
		s = new MyPStack(10);
		db = mock(IDataBase.class);
	}

	@Test
	public void canCreateStackWithoutSpecifyingSize() {
		s = new MyPStack();
		assertThat(s, is(notNullValue()));
	}

	@Test
	public void stackIsEmptyOnConstruction() {
		assertTrue(s.isEmpty());
	}

	@Test
	public void canSetTheCapacityOfStack() {
		MyPStack s = new MyPStack(100);
		assertThat(s.maxSize(), is(equalTo(100)));
	}

	@Test
	public void stackHasSizeZeroOnConstruction() {
		assertThat(s.size(), is(equalTo(0)));
	}

	@Test /* testing boundary behavior */
	public void after1PushStackIsNonEmptyAndSizeIs1() throws OverflowException {
		s.push(1);
		assertFalse(s.isEmpty());
		assertThat(s.size(), is(equalTo(1)));
	}

	@Test /* testing nominal behavior */
	public void afterNPushesStackSizeIsN() throws OverflowException {
		int n = 3;
		for (int i = 1; i <= n; i++) {
			s.push(i * 100);
		}
		assertFalse(s.isEmpty());
		assertThat(s.size(), is(equalTo(n)));
	}

	@Test
	public void popAfterPushReturnsPushedValueAndRestoresStackSize()
			throws OverflowException, InvalidOperationException {
		int pushValue = 200;
		int oldSize = s.size();
		s.push(pushValue);
		assertThat(s.pop(), is(equalTo(pushValue)));
		assertThat(s.size(), is(equalTo(oldSize)));
	}

	@Test
	public void peekAfterPushReturnsPushedValueAndMaintainsStackSize()
			throws OverflowException, InvalidOperationException {
		int pushValue = 300;
		s.push(pushValue);
		int size = s.size();
		assertThat(s.peek(), is(equalTo(pushValue)));
		assertThat(s.size(), is(equalTo(size)));
	}

	public void poppingAllValuesLeavesAnEmptyStack()
			throws OverflowException, InvalidOperationException {
		int size = 5;
		for (int v = 1; v <= size; v++) {
			s.push(v);
		}
		for (int v = 1; v <= size; v++) {
			s.pop();
		}
		assertTrue(s.isEmpty());
	}

	@Test(expected = InvalidOperationException.class)
	public void poppingFromEmptyStackThrowsException() throws InvalidOperationException {
		/* this will fail... naturally */
		s.pop();
	}

	@Test(expected = InvalidOperationException.class)
	public void peekingIntoEmptyStackThrowsException() throws InvalidOperationException {
		/* this will fail... naturally */
		s.peek();
	}

	@Test(expected = OverflowException.class)
	public void pushingTooManyElementsToStackThrowsException() throws OverflowException {
		for (int v = 1; v <= s.maxSize() + 1; v++) {
			s.push(v);
		}
	}

	@Test
	public void pushingTooManyElementsToStackThrowsExceptionSaferVersion() throws OverflowException {
		int lastDrop = 10;
		for (int v = 1; v <= s.maxSize(); v++) {
			s.push(v);
		}
		try {
			s.push(lastDrop);
			fail(); // hmmm, this should never happen
		} catch (OverflowException e) {
			// success: do nothing
		}
	}

	@Test
	public void canCreatePStackWithMockedDB() {
		MyPStack stack = new MyPStack(db);
		assertThat(stack, is(notNullValue()));
	}

	@Test
	public void stackHasNonNullIdOnCreation() {

	}

	@Test
	public void creatingMultipleStacksGeneratesUniqueIDs() {
	}
	// @Test void

	@Test
	public void initallyThereIsNoEntryInDb() {
		new MyPStack(db);
		verify(db, never()).create(anyString(), anyInt());
	}

	@Test
	public void pushSavesTopInDBDuringFirstPush() throws OverflowException {
		MyPStack stack = new MyPStack(db);
		stack.push(42);

		verify(db).create(anyString(), anyInt());
	}

	@Test
	public void pushUpdatesTopInDBInConsecutivePush() throws OverflowException, InvalidOperationException {
		MyPStack stack = new MyPStack(db);

		stack.push(42);
		stack.push(69);

		verify(db).create(stack.getId(), 42);
		verify(db).update(stack.getId(), 69);
	}

	@Test
	public void popUpdatesTopInDB() throws OverflowException, InvalidOperationException {
		MyPStack stack = new MyPStack(db);
		stack.push(42);
		stack.push(69);
		stack.pop();

		InOrder inOrder = inOrder(db);
		inOrder.verify(db).update(stack.getId(), 69);
		inOrder.verify(db).update(stack.getId(), 42);
	}

	@Test
	public void resetReadsRightValueFromDB() throws OverflowException {
		MyPStack stack = new MyPStack(db);

		stack.push(42);
		stack.push(69);
		stack.reset();

		verify(db).read(stack.getId());
	}

	@Test
	public void resettingMyPStackWithoutDBDoesNothin() throws OverflowException {
		MyPStack stack = new MyPStack();
		stack.reset();
		verifyNoInteractions(db);
	}

	@Test
	public void afterResetStackHasOnlyLastTopElement() throws OverflowException, InvalidOperationException {
		MyPStack stack = new MyPStack(db);
		// Pretend the last top value is 100
		when(db.read(stack.getId())).thenReturn(100);

		stack.push(42);
		stack.push(69);

		stack.reset();
		assertThat(stack.peek(), is(equalTo(100)));
	}

	@Test
	public void whenStackBecomesEmptyDBEntryIsDeleted() throws OverflowException, InvalidOperationException {
		MyPStack stack = new MyPStack(db);
		stack.push(42);
		stack.pop();

		verify(db).delete(stack.getId());
	}

	@Test
	public void resettingEmptyStackDoesNothing() {
		MyPStack stack = new MyPStack(db);
		stack.reset();
		verifyNoInteractions(db);
	}

}
