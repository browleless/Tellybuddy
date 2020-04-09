/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.DiscountCode;
import java.util.List;
import javax.ejb.Local;
import util.exception.DiscountCodeAlreadyExpiredException;
import util.exception.DiscountCodeExistException;
import util.exception.DiscountCodeNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author ngjin
 */
@Local
public interface DiscountCodeSessionBeanLocal {

    public Long createNewDiscountCode(DiscountCode discountCode) throws UnknownPersistenceException, DiscountCodeExistException, InputDataValidationException;

    public DiscountCode retrieveDiscountCodeByDiscountCodeId(Long discountCodeId) throws DiscountCodeNotFoundException;

    public List<DiscountCode> retrieveAllDiscountCodes();

    public List<DiscountCode> retrieveAllActiveDiscountCodes();

    public void updateDiscountCode(DiscountCode dc) throws DiscountCodeAlreadyExpiredException, DiscountCodeNotFoundException;

    public void deleteDiscountCode(Long discountCodeId) throws DiscountCodeNotFoundException;

    public DiscountCode retrieveDiscountCodeByDiscountCodeName(String discountCodeName) throws DiscountCodeNotFoundException;

    public List<DiscountCode> retrieveAllPastDiscountCodes();

}
