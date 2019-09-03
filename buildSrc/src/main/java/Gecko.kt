/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

internal object GeckoVersions {
    /**
     * GeckoView Nightly Version.
     */
    const val nightly_version = "70.0.20190901094958"

    /**
     * GeckoView Beta Version.
     */
    const val beta_version = "69.0.20190824140027"

    /**
     * GeckoView Release Version.
     */
    const val release_version = "69.0.20190903125908"
}

@Suppress("MaxLineLength")
object Gecko {
    const val geckoview_nightly = "org.mozilla.geckoview:geckoview-nightly:${GeckoVersions.nightly_version}"

    const val geckoview_beta = "org.mozilla.geckoview:geckoview-beta:${GeckoVersions.beta_version}"

    const val geckoview_release_arm = "org.mozilla.geckoview:geckoview-armeabi-v7a:${GeckoVersions.release_version}"
    const val geckoview_release_x86 = "org.mozilla.geckoview:geckoview-x86:${GeckoVersions.release_version}"
    const val geckoview_release_aarch64 = "org.mozilla.geckoview:geckoview-arm64-v8a:${GeckoVersions.release_version}"
}
