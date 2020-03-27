/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Bill;
import java.util.concurrent.Future;
import javax.ejb.Local;

/**
 *
 * @author tjle2
 */
@Local
public interface EmailSessionBeanLocal {

    public Future<Boolean> emailBillNotificationAsync(Bill bill, Integer subscriptionTotalAllowedData, Integer subscriptionTotalAllowedSms, Integer subscriptionTotalAllowedTalktime, String fromEmailAddress, String toEmailAddress) throws InterruptedException;
    
}
