/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.firebase.ui.auth;

import android.content.Intent;

import com.firebase.ui.auth.AuthUI.IdpConfig;
import com.firebase.ui.auth.AuthUI.SignInIntentBuilder;
import com.firebase.ui.auth.test_helpers.CustomRobolectricGradleTestRunner;
import com.firebase.ui.auth.test_helpers.TestConstants;
import com.firebase.ui.auth.test_helpers.TestHelper;
import com.firebase.ui.auth.ui.ExtraConstants;
import com.firebase.ui.auth.ui.FlowParameters;
import com.google.firebase.FirebaseApp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;

@RunWith(CustomRobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class AuthUITest {
    private FirebaseApp mFirebaseApp;

    @Before
    public void setUp() {
        mFirebaseApp = TestHelper.initializeApp(RuntimeEnvironment.application);
    }

    @Test
    public void testCreateStartIntent_deprecatedSetProvidersShouldStillWork() {
        Intent startIntent = AuthUI
                .getInstance(mFirebaseApp)
                .createSignInIntentBuilder()
                .setProviders(new String[] {AuthUI.EMAIL_PROVIDER, AuthUI.TWITTER_PROVIDER})
                .build();
        FlowParameters flowParameters =
                startIntent.getParcelableExtra(ExtraConstants.EXTRA_FLOW_PARAMS);
        assertEquals(2, flowParameters.providerInfo.size());
        assertEquals(AuthUI.EMAIL_PROVIDER, flowParameters.providerInfo.get(0).getProviderId());
    }

    @Test
    public void testCreateStartIntent_shouldHaveEmailAsDefaultProvider() {
        Intent startIntent = AuthUI
                .getInstance(mFirebaseApp)
                .createSignInIntentBuilder()
                .build();
        FlowParameters flowParameters =
                startIntent.getParcelableExtra(ExtraConstants.EXTRA_FLOW_PARAMS);
        assertEquals(1, flowParameters.providerInfo.size());
        assertEquals(AuthUI.EMAIL_PROVIDER, flowParameters.providerInfo.get(0).getProviderId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateStartIntent_shouldOnlyAllowOneInstanceOfAnIdp() {
        SignInIntentBuilder startIntent =
                AuthUI.getInstance(mFirebaseApp).createSignInIntentBuilder();
        startIntent.setProviders(
                Arrays.asList(
                        new IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                        new IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()));
    }

    @Test
    public void testCreatingStartIntent() {
        Intent startIntent = AuthUI.getInstance(mFirebaseApp).createSignInIntentBuilder()
                .setProviders(AuthUI.EMAIL_PROVIDER, AuthUI.GOOGLE_PROVIDER)
                .setTosUrl(TestConstants.TOS_URL)
                .build();

        FlowParameters flowParameters = startIntent.getParcelableExtra(
                ExtraConstants.EXTRA_FLOW_PARAMS);

        assertEquals(2, flowParameters.providerInfo.size());
        assertEquals(mFirebaseApp.getName(), flowParameters.appName);
        assertEquals(TestConstants.TOS_URL, flowParameters.termsOfServiceUrl);
        assertEquals(AuthUI.getDefaultTheme(), flowParameters.themeId);
    }
}
