package ch.esmeralda.quasimodo;

public class Order {
	private String orderName;
    private String orderStatus;
    
    public Order(){};
    
    public Order(String name, String status) {
    	this.orderName = name;
    	this.orderStatus = status;
    }
    
    public String getOrderName() {
        return orderName;
    }
    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }
    public String getOrderStatus() {
        return orderStatus;
    }
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
    
}
