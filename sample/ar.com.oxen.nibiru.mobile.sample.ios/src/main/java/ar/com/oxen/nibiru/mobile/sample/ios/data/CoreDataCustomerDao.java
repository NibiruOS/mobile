package ar.com.oxen.nibiru.mobile.sample.ios.data;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.inject.Inject;

import org.robovm.apple.coredata.NSEntityDescription;
import org.robovm.apple.coredata.NSFetchRequest;
import org.robovm.apple.coredata.NSManagedObject;
import org.robovm.apple.coredata.NSManagedObjectContext;
import org.robovm.apple.foundation.NSNumber;
import org.robovm.apple.foundation.NSPredicate;
import org.robovm.apple.foundation.NSString;

import ar.com.oxen.nibiru.mobile.core.api.async.Callback;
import ar.com.oxen.nibiru.mobile.sample.app.api.data.Customer;
import ar.com.oxen.nibiru.mobile.sample.app.api.data.CustomerDao;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public class CoreDataCustomerDao implements CustomerDao {
	private final static String ENTITY = "Customer";
	private final static String CUSTOMER_ID = "customerId";
	private final static String FIRST_NAME = "firstName";
	private final static String LAST_NAME = "lastName";
	private final static Function<NSManagedObject, Customer> ENTITY_TO_CUSTOMER = new Function<NSManagedObject, Customer>() {
		@Override
		public Customer apply(NSManagedObject managedObject) {
			NSNumber customerId = (NSNumber) managedObject
					.getValue(CUSTOMER_ID);
			NSString firstName = (NSString) managedObject.getValue(FIRST_NAME);
			NSString lastName = (NSString) managedObject.getValue(LAST_NAME);
			return new CustomerImpl(customerId != null ? customerId.intValue()
					: null, firstName != null ? firstName.toString() : null,
					lastName != null ? lastName.toString() : null);
		}
	};
	private final NSManagedObjectContext managedObjectContext;

	@Inject
	public CoreDataCustomerDao(NSManagedObjectContext managedObjectContext) {
		this.managedObjectContext = Preconditions
				.checkNotNull(managedObjectContext);
	}

	@Override
	public void findAll(Callback<List<Customer>> callback) {
		checkNotNull(callback);
		try {
			callback.onSuccess(ImmutableList.copyOf(Iterables.transform(
					managedObjectContext
							.executeFetchRequest(new NSFetchRequest(ENTITY)),
					ENTITY_TO_CUSTOMER)));
		} catch (Exception e) {
			callback.onFailure(e);
		}
	}

	@Override
	public void findById(Integer id, Callback<Customer> callback) {
		checkNotNull(id);
		checkNotNull(callback);
		try {
			callback.onSuccess(Iterables.getFirst(Iterables.transform(
					managedObjectContext.executeFetchRequest(byId(id)),
					ENTITY_TO_CUSTOMER), null));
		} catch (Exception e) {
			callback.onFailure(e);
		}
	}

	@Override
	public Customer create() {
		return new CustomerImpl(null, null, null);
	}

	@Override
	public void save(Customer customer, Callback<Void> callback) {
		checkNotNull(customer);
		checkNotNull(callback);
		try {
			CustomerImpl customerImpl = (CustomerImpl) customer;
			NSManagedObject managedObject = null;
			if (!customerImpl.isNew()) {
				managedObject = Iterables.getFirst(managedObjectContext
						.executeFetchRequest(byId(customer.getCustomerId())),
						null);
			}
			if (managedObject == null) {
				managedObject = new NSManagedObject(
						NSEntityDescription.getEntityByNameInContext(ENTITY,
								managedObjectContext), managedObjectContext);
			}
			if (customerImpl.isNew()) {
				managedObjectContext.save();
				customerImpl.setCustomerId((int)managedObject.getObjectID().hash());
			}
			managedObject.setValue(CUSTOMER_ID,
					NSNumber.valueOf(customer.getCustomerId()));
			managedObject.setValue(
					FIRST_NAME,
					customer.getFirstName() != null ? new NSString(customer
							.getFirstName()) : null);
			managedObject.setValue(
					LAST_NAME,
					customer.getLastName() != null ? new NSString(customer
							.getLastName()) : null);
			managedObjectContext.save();
			callback.onSuccess(null);
		} catch (Exception e) {
			callback.onFailure(e);
		}
	}

	private NSFetchRequest byId(int id) {
		NSFetchRequest request = new NSFetchRequest(ENTITY);
		request.setPredicate(NSPredicate.create("customerId == %@",
				NSNumber.valueOf(id)));
		return request;
	}
}
