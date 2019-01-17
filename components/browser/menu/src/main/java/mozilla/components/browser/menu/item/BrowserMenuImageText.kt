/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.browser.menu.item

import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatImageView
import android.view.View
import android.widget.TextView
import mozilla.components.browser.menu.BrowserMenu
import mozilla.components.browser.menu.BrowserMenuItem
import mozilla.components.browser.menu.R

private const val NO_ID = -1

/**
 * A menu item for displaying text with an image icon.
 *
 * @param label The visible label of this menu item.
 * @param imageResource ID of a drawable resource to be shown as icon.
 * @param contentDescription The image's content description, used for accessibility support.
 * @param iconTintColorResource Optional ID of color resource to tint the icon.
 * @param listener Callback to be invoked when this menu item is clicked.
 */
class BrowserMenuImageText(
    private val label: String,
    @DrawableRes
    private val imageResource: Int,
    private val contentDescription: String,
    @ColorRes
    private val iconTintColorResource: Int = NO_ID,
    private val listener: () -> Unit = {}
) : BrowserMenuItem {

    override var visible: () -> Boolean = { true }

    override fun getLayoutResource() = R.layout.mozac_browser_menu_item_image_text

    override fun bind(menu: BrowserMenu, view: View) {

        bindText(view)

        bindImage(view)

        view.setOnClickListener {
            listener.invoke()
            menu.dismiss()
        }
    }

    private fun bindText(view: View) {
        val textView = view.findViewById<TextView>(R.id.text)
        textView.text = label
    }

    private fun bindImage(view: View) {
        val imageView = view.findViewById<AppCompatImageView>(R.id.image)

        with(imageView) {
            setImageResource(imageResource)
            contentDescription = this@BrowserMenuImageText.contentDescription

            if (iconTintColorResource != NO_ID) {
                imageView.imageTintList = ContextCompat.getColorStateList(view.context, iconTintColorResource)
            }
        }
    }
}
