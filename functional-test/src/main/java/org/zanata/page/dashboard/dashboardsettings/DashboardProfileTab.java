/*
 * Copyright 2014, Red Hat, Inc. and individual contributors as indicated by the
 * @author tags. See the copyright.txt file in the distribution for a full
 * listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.zanata.page.dashboard.dashboardsettings;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.zanata.page.dashboard.DashboardBasePage;

/**
 * @author Damian Jansen
 * <a href="mailto:camunoz@redhat.com">djansen@redhat.com</a>
 */
@Slf4j
public class DashboardProfileTab extends DashboardBasePage {

    @FindBy(id = "profileForm:nameField:accountName")
    private WebElement accountNameField;

    @FindBy(id = "updateProfileButton")
    private WebElement updateProfileButton;

    public DashboardProfileTab(WebDriver driver) {
        super(driver);
    }

    public String getUsername() {
        log.info("Query user name");
        return getDriver().findElement(By.id("profileForm"))
                .findElement(By.className("l--push-bottom-0"))
                .getText();
    }

    public DashboardProfileTab enterName(String name) {
        log.info("Enter name {}", name);
        accountNameField.clear();
        accountNameField.sendKeys(name);
        return this;
    }

    public DashboardProfileTab clickUpdateProfileButton() {
        log.info("Click Update");
        updateProfileButton.click();
        return this;
    }
}
