import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Circular doubly-linked lists.
 *
 * These do support the Fail Fast policy.
 */
public class SimpleCDLL<T> implements SimpleList<T> {
  // +--------+------------------------------------------------------------
  // | Fields |
  // +--------+

  private Node2<T> dummyNode2;
  /**
   * The front of the list
   */
  Node2<T> front;

  /**
   * The number of values in the list.
   */
  int size;

  /**
   * Fail fast makes sure that if one iterator modifies the list then the others shouldn't be able
   * to The counter keeps track of the number of modificatons made by all counters
   */
  int numModifications;

  // +--------------+------------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create an empty list.
   */
  public SimpleCDLL() {
    this.size = 0;
    // Create the dummy node
    SimpleCDLL.this.dummyNode2 = new Node2<T>(null);
    SimpleCDLL.this.dummyNode2.next = SimpleCDLL.this.dummyNode2;
    SimpleCDLL.this.dummyNode2.prev = SimpleCDLL.this.dummyNode2;

    SimpleCDLL.this.front = dummyNode2.next;
  } // SimpleDLL

  // +-----------+---------------------------------------------------------
  // | Iterators |
  // +-----------+

  public Iterator<T> iterator() {
    return listIterator();
  } // iterator()

  public ListIterator<T> listIterator() {
    return new ListIterator<T>() {
      // +--------+--------------------------------------------------------
      // | Fields |
      // +--------+

      /**
       * The position in the list of the next value to be returned. Included because ListIterators
       * must provide nextIndex and prevIndex.
       */
      int pos = 0;

      /**
       * The cursor is between neighboring values, so we start links to the previous and next
       * value..
       */
      Node2<T> prev = dummyNode2;
      Node2<T> next = SimpleCDLL.this.front;

      /**
       * The node to be updated by remove or set. Has a value of null when there is no such value.
       */
      Node2<T> update = null;

      /**
       * Fail fast makes sure that if one iterator modifies the list then the others shouldn't be
       * able to The counter keeps track of the number of modificatons made by all counters
       */
      int curModifications = numModifications;


      // +---------+-------------------------------------------------------
      // | Methods |
      // +---------+

      /**
       * Adds an element with value val to the list where the iterator is by modifying the list
       * 
       * @exception ConcurrentModificationException when the list has been modified by a different
       *            iterator since this iterator was created
       * @exception IllegalStateException when remove() or add(V) has been called since the last call to previous() or next()
       */
      public void add(T val) throws ConcurrentModificationException {
        // Has the list been modified since the iterator was created?
        if (!(this.curModifications == SimpleCDLL.this.numModifications)) {
          throw new ConcurrentModificationException();
        } // if

        // Normal case:
        this.prev = this.prev.insertAfter(val);
        SimpleCDLL.this.front = dummyNode2.next;

        // Note that we cannot update
        this.update = null;

        // Increase the size
        ++SimpleCDLL.this.size;

        // Update the position.
        ++this.pos;

        // Update current modifications for the list
        ++SimpleCDLL.this.numModifications;

        // Update current modifications for the iterator
        ++this.curModifications;
      } // add(T)


      /**
       * Returns true if there is an subsequent element in the list
       * 
       * @exception ConcurrentModificationException when the list has been modified by a different
       *            iterator since this iterator was created
       */
      public boolean hasNext() throws ConcurrentModificationException {
        // Has the list been modified since the iterator was created?
        if (!(this.curModifications == SimpleCDLL.this.numModifications)) {
          throw new ConcurrentModificationException();
        } // if

        return (this.pos < SimpleCDLL.this.size);
      } // hasNext()


      /**
       * Returns true if there is an previous element in the list
       * 
       * @exception ConcurrentModificationException when the list has been modified by a different
       *            iterator since this iterator was created
       */
      public boolean hasPrevious() throws ConcurrentModificationException {
        // Has the list been modified since the iterator was created?
        if (!(this.curModifications == SimpleCDLL.this.numModifications)) {
          throw new ConcurrentModificationException();
        } // if

        return (this.pos > 0);
      } // hasPrevious()


      /**
       * Returns the next element in the list and moves the iterator forward
       * 
       * @exception ConcurrentModificationException when the list has been modified by a different
       *            iterator since this iterator was created
       * @exception NoSuchElementException when there is not a next element
       */
      public T next() throws ConcurrentModificationException {
        // Has the list been modified since the iterator was created?
        if (!(this.curModifications == SimpleCDLL.this.numModifications)) {
          throw new ConcurrentModificationException();
        }
        if (!this.hasNext()) {
          throw new NoSuchElementException();
        } // if
        // Identify the node to update
        this.update = this.next;
        // Advance the cursor
        this.prev = this.next;
        this.next = this.next.next;
        // Note the movement
        ++this.pos;
        // And return the value
        return this.update.value;
      } // next()


      /**
       * Returns the index of the iterator's next element
       * 
       * @exception ConcurrentModificationException when the list has been modified by a different
       *            iterator since this iterator was created
       */
      public int nextIndex() throws ConcurrentModificationException {
        // Has the list been modified since the iterator was created?
        if (!(this.curModifications == SimpleCDLL.this.numModifications)) {
          throw new ConcurrentModificationException();
        }
        return this.pos;
      } // nextIndex()


      /**
       * Returns the index of the iterator's previous element
       * 
       * @exception ConcurrentModificationException when the list has been modified by a different
       *            iterator since this iterator was created
       */
      public int previousIndex() throws ConcurrentModificationException {
        // Has the list been modified since the iterator was created?
        if (!(this.curModifications == SimpleCDLL.this.numModifications)) {
          throw new ConcurrentModificationException();
        }
        return this.pos - 1;
      } // prevIndex


      /**
       * Returns the previous element in the list and moves the iterator backward
       * 
       * @exception ConcurrentModificationException when the list has been modified by a different
       *            iterator since this iterator was created
       * @exception NoSuchElementException when there is not a previous element
       */
      public T previous() throws NoSuchElementException, ConcurrentModificationException {
        // Has the list been modified since the iterator was created?
        if (!(this.curModifications == SimpleCDLL.this.numModifications)) {
          throw new ConcurrentModificationException();
        }
        if (!this.hasPrevious())
          throw new NoSuchElementException();
        // STUB
        return null;
      } // previous()


      /**
       * Removes the last returned element of the list. May be the element before or after the iterator depending on the last call to previous() or next()
       * 
       * @exception ConcurrentModificationException when the list has been modified by a different
       *            iterator since this iterator was created
       * @exception IllegalStateException when remove() or add(V) has been called since the last call to previous() or next()
       */
      public void remove() throws ConcurrentModificationException {
        // Has the list been modified since the iterator was created?
        if (!(this.curModifications == SimpleCDLL.this.numModifications)) {
          throw new ConcurrentModificationException();
        }
        // Sanity check
        if (this.update == null) {
          throw new IllegalStateException();
        } // if

        // Update the cursor
        if (this.next == this.update) {
          this.next = this.update.next;
        } // if
        if (this.prev == this.update) {
          this.prev = this.update.prev;
          --this.pos;
        } // if

        // Do the real work
        this.update.remove();
        --SimpleCDLL.this.size;
        SimpleCDLL.this.front = dummyNode2.next;

        // Note that no more updates are possible
        this.update = null;

        // Update current modifications for the list
        ++SimpleCDLL.this.numModifications;

        // Update current modifications for the iterator
        ++this.curModifications;

      } // remove()

      /**
       * Returns true if there is an subsequent element in the list
       * 
       * @exception ConcurrentModificationException when the list has been modified by a different
       *            iterator since this iterator was created
       * @exception IllegalStateException when remove() or add(V) has been called since the last call to previous() or next()
       */
      public void set(T val) throws ConcurrentModificationException {
        // Has the list been modified since the iterator was created?
        if (!(this.curModifications == SimpleCDLL.this.numModifications)) {
          throw new ConcurrentModificationException();
        }
        // Sanity check
        if (this.update == null || this.update.equals(SimpleCDLL.this.front)) {
          throw new IllegalStateException();
        } // if
        // Do the real work
        this.update.value = val;
        // Note that no more updates are possible
        this.update = null;
      } // set(T)
    };
  } // listIterator()

} // class SimpleDLL<T>
