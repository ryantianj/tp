package seedu.address.model.contact;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.contact.exceptions.ContactNotFoundException;
import seedu.address.model.contact.exceptions.DuplicateContactException;


/**
 * A list of contacts that enforces uniqueness between its elements and does not allow nulls.
 * A contact is considered unique by comparing using {@code Contact#isSameContact(Contact)}. As such, adding and
 * updating of contacts uses Contact#isSameContact(Contact) for equality so as to ensure that the contact being added
 * or updated isunique in terms of identity in the UniqueContactList. However, the removal of a contact uses
 * Contact#equals(Object) so as to ensure that the contact with exactly the same fields will be removed.
 *
 * Supports a minimal set of list operations.
 *
 * @see Contact#isSameContact(Contact)
 */
public class UniqueContactList implements Iterable<Contact> {

    private final ObservableList<Contact> internalList = FXCollections.observableArrayList();
    private final ObservableList<Contact> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);

    /**
     * Returns true if the list contains an equivalent contact as the given argument.
     */
    public boolean contains(Contact toCheck) {
        requireNonNull(toCheck);
        return internalList.stream().anyMatch(toCheck::isSameContact);
    }

    /**
     * Adds a contact to the list.
     * The contact must not already exist in the list.
     */
    public void add(Contact toAdd) {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicateContactException();
        }
        internalList.add(toAdd);
    }

    /**
     * Replaces the contact {@code target} in the list with {@code editedContact}.
     * {@code target} must exist in the list.
     * The contact identity of {@code editedContact} must not be the same as another existing contact in the list.
     */
    public void setContact(Contact target, Contact editedContact) {
        requireAllNonNull(target, editedContact);

        int index = internalList.indexOf(target);
        if (index == -1) {
            throw new ContactNotFoundException();
        }

        if (!target.isSameContact(editedContact) && contains(editedContact)) {
            throw new DuplicateContactException();
        }

        internalList.set(index, editedContact);
    }

    /**
     * Removes the equivalent contact from the list.
     * The contact must exist in the list.
     */
    public void remove(Contact toRemove) {
        requireNonNull(toRemove);
        if (!internalList.remove(toRemove)) {
            throw new ContactNotFoundException();
        }
    }

    public void setContacts(UniqueContactList replacement) {
        requireNonNull(replacement);
        internalList.setAll(replacement.internalList);
    }

    /**
     * Replaces the contents of this list with {@code contacts}.
     * {@code contacts} must not contain duplicate contacts.
     */
    public void setContacts(List<Contact> contacts) {
        requireAllNonNull(contacts);
        if (!contactsAreUnique(contacts)) {
            throw new DuplicateContactException();
        }

        internalList.setAll(contacts);
    }

    /**
     * Sorts the contents of the list by {@code sortBy}.
     * If {@code sortBy} is "name", it sorts by name in alphabetical order.
     * If {@code sortBy} is "rating", it sorts by rating in descending order.
     */
    public void sortList(String sortBy) {
        requireNonNull(sortBy);
        Comparator<Contact> compare = null;
        if (sortBy == "name") {
            compare = Comparator.comparing(Contact::getName);
        } else if (sortBy == "rating") {
            compare = Comparator.comparing(Contact::getRating).reversed();
        }
        internalList.sort(compare);
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<Contact> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    @Override
    public Iterator<Contact> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof UniqueContactList // instanceof handles nulls
                        && internalList.equals(((UniqueContactList) other).internalList));
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

    /**
     * Returns true if {@code contacts} contains only unique contacts.
     */
    private boolean contactsAreUnique(List<Contact> contacts) {
        for (int i = 0; i < contacts.size() - 1; i++) {
            for (int j = i + 1; j < contacts.size(); j++) {
                if (contacts.get(i).isSameContact(contacts.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }
}
