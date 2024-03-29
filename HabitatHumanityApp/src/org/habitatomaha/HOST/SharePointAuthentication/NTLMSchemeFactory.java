package org.habitatomaha.HOST.SharePointAuthentication;

import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthSchemeFactory;
import org.apache.http.impl.auth.NTLMScheme;
import org.apache.http.params.HttpParams;

//http://hc.apache.org/httpcomponents-client-ga/ntlm.html
public class NTLMSchemeFactory implements AuthSchemeFactory {

    @Override
	public AuthScheme newInstance(final HttpParams params) {
        return new NTLMScheme(new JCIFSEngine());
    }

}