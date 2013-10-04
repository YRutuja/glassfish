/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package org.glassfish.cdi.transaction;

import junit.framework.TestCase;

import javax.transaction.InvalidTransactionException;
import javax.transaction.TransactionRequiredException;
import javax.transaction.TransactionalException;

/**
 * User: paulparkinson
 * Date: 12/10/12
 * Time: 3:50 PM
 */
public class TransactionalAnnotationTest extends TestCase {

    public static void main(String args[]) throws Exception {
        TransactionalAnnotationTest transactionalAnnotationTest =
                new TransactionalAnnotationTest();
        transactionalAnnotationTest.testTransactionalInterceptorMANDATORY();
        transactionalAnnotationTest.testTransactionalInterceptorNEVER();
        transactionalAnnotationTest.testTransactionalInterceptorNOT_SUPPORTED();
        transactionalAnnotationTest.testTransactionalInterceptorREQUIRED();
        transactionalAnnotationTest.testTransactionalInterceptorREQUIRES_NEW();
        transactionalAnnotationTest.testTransactionalInterceptorSUPPORTS();
    }

    public void testTransactionalInterceptorMANDATORY() throws Exception {
        TransactionalInterceptorMandatory transactionalInterceptorMANDATORY =
                new TransactionalInterceptorMandatory();
        javax.transaction.TransactionManager transactionManager = new TransactionManager();
        transactionalInterceptorMANDATORY.setTestTransactionManager(transactionManager);
        javax.interceptor.InvocationContext ctx =
                new InvocationContext(
                        BeanMandatory.class.getMethod("foo", String.class), null
                );
        try {
            transactionalInterceptorMANDATORY.transactional(ctx);
            fail("should have thrown TransactionRequiredException due to " +
                    "transactionalInterceptorMANDATORY and no tx in place");
        } catch (TransactionalException transactionalException) {
            assertTrue(
                    "transactionalException.getCause() instanceof TransactionRequiredException",
                    transactionalException.getCause() instanceof TransactionRequiredException);
        }
        transactionManager.begin();
        transactionalInterceptorMANDATORY.transactional(ctx);
        transactionManager.commit();
        try {
            transactionalInterceptorMANDATORY.transactional(ctx);
            fail("should have thrown TransactionRequiredException due to " +
                    "transactionalInterceptorMANDATORY and no tx in place");
        } catch (TransactionalException transactionalException) {
            assertTrue(
                    "transactionalException.getCause() instanceof TransactionRequiredException",
                    transactionalException.getCause() instanceof TransactionRequiredException);
        }
    }

    public void testTransactionalInterceptorNEVER() throws Exception {
        TransactionalInterceptorNever transactionalInterceptorNEVER =
                new TransactionalInterceptorNever();
        javax.transaction.TransactionManager transactionManager = new TransactionManager();
        transactionalInterceptorNEVER.setTestTransactionManager(transactionManager);
        javax.interceptor.InvocationContext ctx =
                new InvocationContext(
                        BeanNever.class.getMethod("foo", String.class), null
                );
        transactionalInterceptorNEVER.transactional(ctx);
        transactionManager.begin();
        try {
            transactionalInterceptorNEVER.transactional(ctx);
            fail("should have thrown InvalidTransactionException due to " +
                    "TransactionalInterceptorNEVER and  tx in place");
        } catch (TransactionalException transactionalException) {
            assertTrue(
                    "transactionalException.getCause() instanceof InvalidTransactionException",
                    transactionalException.getCause() instanceof InvalidTransactionException);
        } finally {
            transactionManager.rollback();
        }
    }

    public void testTransactionalInterceptorNOT_SUPPORTED() throws Exception {
        TransactionalInterceptorNotSupported transactionalInterceptorNOT_SUPPORTED =
                new TransactionalInterceptorNotSupported();
        javax.transaction.TransactionManager transactionManager = new TransactionManager();
        transactionalInterceptorNOT_SUPPORTED.setTestTransactionManager(transactionManager);
        javax.interceptor.InvocationContext ctx =
                new InvocationContext(
                        BeanNotSupported.class.getMethod("foo", String.class), null
                );
        transactionalInterceptorNOT_SUPPORTED.transactional(ctx);
    }

    public void testTransactionalInterceptorREQUIRED() throws Exception {
        TransactionalInterceptorRequired transactionalInterceptorREQUIRED =
                new TransactionalInterceptorRequired();
        javax.transaction.TransactionManager transactionManager = new TransactionManager();
        transactionalInterceptorREQUIRED.setTestTransactionManager(transactionManager);
        javax.interceptor.InvocationContext ctx =
                new InvocationContext(
                        BeanRequired.class.getMethod("foo", String.class), null
                );
        transactionalInterceptorREQUIRED.transactional(ctx);
        transactionManager.begin();
        transactionalInterceptorREQUIRED.transactional(ctx);
        transactionManager.commit();
        //todo equality check
    }

    public void testTransactionalInterceptorREQUIRES_NEW() throws Exception {
        TransactionalInterceptorRequiresNew transactionalInterceptorREQUIRES_NEW =
                new TransactionalInterceptorRequiresNew();
        javax.transaction.TransactionManager transactionManager = new TransactionManager();
        transactionalInterceptorREQUIRES_NEW.setTestTransactionManager(transactionManager);
        javax.interceptor.InvocationContext ctx =
                new InvocationContext(
                        BeanRequiresNew.class.getMethod("foo", String.class), null
                );
        transactionalInterceptorREQUIRES_NEW.transactional(ctx);
        transactionManager.begin();
        transactionalInterceptorREQUIRES_NEW.transactional(ctx);
        transactionManager.commit();
        //todo equality check
    }

    public void testTransactionalInterceptorSUPPORTS() throws Exception {
        TransactionalInterceptorSupports transactionalInterceptorSUPPORTS =
                new TransactionalInterceptorSupports();
        javax.transaction.TransactionManager transactionManager = new TransactionManager();
        transactionalInterceptorSUPPORTS.setTestTransactionManager(transactionManager);
        javax.interceptor.InvocationContext ctx =
                new InvocationContext(
                        BeanSupports.class.getMethod("foo", String.class), null
                );
        transactionalInterceptorSUPPORTS.transactional(ctx);
        transactionManager.begin();
        transactionalInterceptorSUPPORTS.transactional(ctx);
        transactionManager.commit();
    }

}
