/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV

*/

/*
 * This class is part of the white paper entitled
 * "Digital Signatures for PDF documents"
 * written by Bruno Lowagie
 *
 * For more info, go to: http://itextpdf.com/learn
 */
package com.itextpdf.samples.signatures.chapter05;

import com.itextpdf.signatures.CRLVerifier;
import com.itextpdf.signatures.CertificateVerification;
import com.itextpdf.signatures.OCSPVerifier;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.SignatureUtil;
import com.itextpdf.signatures.VerificationException;
import com.itextpdf.signatures.VerificationOK;
import com.itextpdf.test.annotations.type.SampleTest;

import javax.smartcardio.CardException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.CRL;
import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import static org.junit.Assert.fail;

@Ignore
@Category(SampleTest.class)
public class C5_03_CertificateValidation extends C5_01_SignatureIntegrity {
    public static final String ADOBE = "./src/test/resources/signatures/chapter05/adobeRootCA.cer";
    public static final String CACERT = "./src/test/resources/signatures/chapter05/CACertSigningAuthority.crt";
    public static final String BRUNO = "./src/test/resources/signatures/chapter05/bruno.crt";

    // public static final String EXAMPLE1 = "results/chapter3/hello_cacert_ocsp_ts.pdf"; // TODO Uncomment after C3_09_SignWithTSA revision
    // public static final String EXAMPLE2 = "results/chapter3/hello_token.pdf"; // TODO Uncomment after C3_11_SignWithToken revision
    public static final String EXAMPLE3 = "./src/test/resources/signatures/chapter05/hello_signed1.pdf";
    // public static final String EXAMPLE4 = "results/chapter4/hello_smartcard_Signature.pdf"; // TODO Uncomment after C4_03_SignWithPKCS11SC revision

    public static final  String expectedOutput = ""; //TODO

    KeyStore ks;

    public PdfPKCS7 verifySignature(SignatureUtil signUtil, String name)
            throws GeneralSecurityException, IOException {
        PdfPKCS7 pkcs7 = super.verifySignature(signUtil, name);
        Certificate[] certs = pkcs7.getSignCertificateChain();
        Calendar cal = pkcs7.getSignDate();
        List<VerificationException> errors = CertificateVerification.verifyCertificates(certs, ks, cal);
        if (errors.size() == 0)
            System.out.println("Certificates verified against the KeyStore");
        else
            System.out.println(errors);
        for (int i = 0; i < certs.length; i++) {
            X509Certificate cert = (X509Certificate) certs[i];
            System.out.println("=== Certificate " + i + " ===");
            showCertificateInfo(cert, cal.getTime());
        }
        X509Certificate signCert = (X509Certificate) certs[0];
        X509Certificate issuerCert = (certs.length > 1 ? (X509Certificate) certs[1] : null);
        System.out.println("=== Checking validity of the document at the time of signing ===");
        checkRevocation(pkcs7, signCert, issuerCert, cal.getTime());
        System.out.println("=== Checking validity of the document today ===");
        checkRevocation(pkcs7, signCert, issuerCert, new Date());
        return pkcs7;
    }

    public static void checkRevocation(PdfPKCS7 pkcs7, X509Certificate signCert, X509Certificate issuerCert, Date date) throws GeneralSecurityException, IOException {
        List<BasicOCSPResp> ocsps = new ArrayList<BasicOCSPResp>();
        if (pkcs7.getOcsp() != null)
            ocsps.add(pkcs7.getOcsp());
        OCSPVerifier ocspVerifier = new OCSPVerifier(null, ocsps);
        List<VerificationOK> verification =
                ocspVerifier.verify(signCert, issuerCert, date);
        if (verification.size() == 0) {
            List<X509CRL> crls = new ArrayList<X509CRL>();
            if (pkcs7.getCRLs() != null) {
                for (CRL crl : pkcs7.getCRLs())
                    crls.add((X509CRL) crl);
            }
            CRLVerifier crlVerifier = new CRLVerifier(null, crls);
            verification.addAll(crlVerifier.verify(signCert, issuerCert, date));
        }
        if (verification.size() == 0) {
            System.out.println("The signing certificate couldn't be verified");
        } else {
            for (VerificationOK v : verification)
                System.out.println(v);
        }
    }

    public void showCertificateInfo(X509Certificate cert, Date signDate) {
        System.out.println("Issuer: " + cert.getIssuerDN());
        System.out.println("Subject: " + cert.getSubjectDN());
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
        System.out.println("Valid from: " + date_format.format(cert.getNotBefore()));
        System.out.println("Valid to: " + date_format.format(cert.getNotAfter()));
        try {
            cert.checkValidity(signDate);
            System.out
                    .println("The certificate was valid at the time of signing.");
        } catch (CertificateExpiredException e) {
            System.out
                    .println("The certificate was expired at the time of signing.");
        } catch (CertificateNotYetValidException e) {
            System.out
                    .println("The certificate wasn't valid yet at the time of signing.");
        }
        try {
            cert.checkValidity();
            System.out.println("The certificate is still valid.");
        } catch (CertificateExpiredException e) {
            System.out.println("The certificate has expired.");
        } catch (CertificateNotYetValidException e) {
            System.out.println("The certificate isn't valid yet.");
        }
    }

    public static void main(String[] args) throws IOException,
            GeneralSecurityException {
        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);
        C5_03_CertificateValidation app = new C5_03_CertificateValidation();
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());

        ks.load(null, null);
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        ks.setCertificateEntry("adobe",
                cf.generateCertificate(new FileInputStream(ADOBE)));
        ks.setCertificateEntry("cacert",
                cf.generateCertificate(new FileInputStream(CACERT)));
        ks.setCertificateEntry("bruno",
                cf.generateCertificate(new FileInputStream(BRUNO)));
        app.setKeyStore(ks);

        // app.verifySignatures(EXAMPLE1);
        // app.verifySignatures(EXAMPLE2);
        app.verifySignatures(EXAMPLE3);
        // app.verifySignatures(EXAMPLE4);
    }

    private void setKeyStore(KeyStore ks) {
        this.ks = ks;
    }

    @Test
    public void runTest() throws GeneralSecurityException, IOException, InterruptedException, CardException {
        new File("./target/test/resources/signatures/chapter05/").mkdirs();
        setupSystemOutput();
        C5_03_CertificateValidation.main(null);
        String sysOut = getSystemOutput();

        if (!sysOut.equals(expectedOutput)) {
            fail("Unexpected output.");
        }
    }
}