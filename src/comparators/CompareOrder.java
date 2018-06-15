package comparators;

import java.util.Comparator;
import utils.Message;

public class CompareOrder implements Comparator<Message>{
	public int compare(Message x, Message y) {
    	int var = ((Integer)x.getOrder()).compareTo(y.getOrder());
    	if(var == 0){
    		var = x.getId().compareTo(y.getId());
    	}
    	return var;
    }
}
	


