package comparators;

import java.util.Comparator;
import utils.Message;

public class CompareOrder implements Comparator<Message>{
    public int compare(Message x, Message y) {
    	return ((Integer)x.getOrder()).compareTo(y.getOrder());
    }
}
	


