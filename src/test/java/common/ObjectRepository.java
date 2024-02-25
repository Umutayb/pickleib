package common;

import pages.FormsPage;
import pages.HomePage;
import pickleib.utilities.interfaces.repository.PageRepository;

public class ObjectRepository implements PageRepository {
    HomePage homePage = new HomePage();
    FormsPage formsPage = new FormsPage();
}
