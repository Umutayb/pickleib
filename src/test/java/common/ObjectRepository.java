package common;

import pages.*;
import pickleib.utilities.interfaces.repository.PageRepository;

public class ObjectRepository implements PageRepository {
    HomePage homePage = new HomePage();
    FormsPage formsPage = new FormsPage();
    InteractionsPage interactionsPage = new InteractionsPage();
    DropDownPage dropDownPage = new DropDownPage();
    TallPage tallPage = new TallPage();
}
