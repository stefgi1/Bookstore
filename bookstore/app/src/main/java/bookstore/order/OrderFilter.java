package bookstore.order;

import java.time.LocalDate;
import java.util.*;

import bookstore.users.User;

public class OrderFilter{
	private List<Order> orders;
	LocalDate after, before;
	User soldBy;
	public OrderFilter(List<Order> orders){
		this.orders = orders;
	}
	public void setAfter(LocalDate after) {
		this.after = after;
	}
	public void setBefore(LocalDate before) {
		this.before = before;
	}
	public void setSoldBy(User soldBy) {
		this.soldBy = soldBy;
	}

	public List<Order> filter(){
		List<Order> ret = new ArrayList<>();
		for (Order order : orders){
			boolean condition =
				(after == null || order.getDate().isAfter(after)) &&
				(before == null || order.getDate().isBefore(before)) &&
				(soldBy == null || order.getSoldBy() == soldBy);
			if (condition)
				ret.add(order);
		}
		return ret;
	}
}
