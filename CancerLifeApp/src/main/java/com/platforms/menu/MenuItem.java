package com.platforms.menu;

public class MenuItem {
	private final String title;
	private final int icon;
    private final String buttonTitle;

	public MenuItem(final String title, final int icon, final String buttonTitle) {
		this.title = title;
		this.icon = icon;
        this.buttonTitle = buttonTitle;
	}

	/**
	 * @return Title of news entry
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return Icon of this news entry
	 */
	public int getIcon() {
		return icon;
	}


    public String getButtonTitle(){
        return buttonTitle;
    }
}
