import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.util.ArrayList;
import java.util.List;

public class OpenWebPage {

    private static WebDriver driver;
    private static ArrayList<SetObject> setObjects = new ArrayList<SetObject>();

    public static void main(String[] args) {
        startBrowser();
        startGame();

        boolean setFound;
        do {
            getElements();
            setFound = findSet();
            setObjects.clear();
        } while(setFound);

        getResults();
        driver.quit();
    }

    private static void startBrowser() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("headless");
        chromeOptions.addArguments("--start-maximized");
        driver = new ChromeDriver(chromeOptions);
        driver.navigate().to("http://smart-games.org/en/set/");
    }

    private static void startGame(){
        WebElement start = driver.findElement(By.xpath("//a[@href='/en/set/start']"));
        start.click();
    }

    private static void getElements() {
        WebElement board = driver.findElement(By.id("board"));
        List<WebElement> webObjects = board.findElements(By.tagName("td"));
        int index = 0;
        for (WebElement elem : webObjects){
            SetObject object = new SetObject();
            String[] splited = elem.findElement(By.tagName("img")).getAttribute("alt").split("\\s+");
            object.symbol = splited[0];
            object.shading = splited[1];
            object.color = splited[2];
            object.number = Integer.parseInt(splited[3]);
            object.id = index;
            setObjects.add(object);
            index++;
        }
    }

    private static boolean findSet(){
        PairObjects elements = rule();
        if(elements == null){
            List<WebElement> openNewCards = driver.findElements(By.id("add_cards"));
            if(openNewCards.size() > 0){
                openNewCards.get(0).click();
                return true;
            }
            return false;
        }
        clickElement(elements);
        return true;
    }

    static class PairObjects{
        SetObject elem1;
        SetObject elem2;
        SetObject elem3;
    }

    private static PairObjects rule(){
        for (int i = 0; i < setObjects.size() ; i++){
            for(int j = i + 1; j < setObjects.size(); j++){
                for(int k = j + 1; k < setObjects.size(); k++){
                    PairObjects pair = new PairObjects();
                    pair.elem1 = setObjects.get(i);
                    pair.elem2 = setObjects.get(j);
                    pair.elem3 = setObjects.get(k);
                    if(sameColor(pair) && sameNumber(pair) && sameSymbols(pair) && sameShading(pair)){
                        return pair;
                    }
                    if(differentColor(pair) && sameNumber(pair) && sameSymbols(pair) && sameShading(pair)){
                        return pair;
                    }
                    if(differentColor(pair) && differentNumber(pair) && sameSymbols(pair) && sameShading(pair)){
                        return pair;
                    }
                    if(differentColor(pair) && sameNumber(pair) && differentSymbols(pair) && sameShading(pair)){
                        return pair;
                    }
                    if(differentColor(pair) && sameNumber(pair) && sameSymbols(pair) && differentNumber(pair)){
                        return pair;
                    }
                    if(differentColor(pair) && differentNumber(pair) && differentSymbols(pair) && sameShading(pair)){
                        return pair;
                    }
                    if(differentColor(pair) && differentNumber(pair) && sameSymbols(pair) && differentShading(pair)){
                        return pair;
                    }
                    if(differentColor(pair) && sameNumber(pair) && differentSymbols(pair) && differentShading(pair)){
                        return pair;
                    }
                    if(differentColor(pair) && differentNumber(pair) && differentSymbols(pair) && differentShading(pair)){
                        return pair;
                    }
                    if(sameColor(pair) && differentNumber(pair) && sameSymbols(pair) && sameShading(pair)){
                        return pair;
                    }
                    if(sameColor(pair) && differentNumber(pair) && differentSymbols(pair) && sameShading(pair)){
                        return pair;
                    }
                    if(sameColor(pair) && differentNumber(pair) && sameSymbols(pair) && differentShading(pair)){
                        return pair;
                    }
                    if(sameColor(pair) && differentNumber(pair) && differentSymbols(pair) && differentShading(pair)){
                        return pair;
                    }
                    if(sameColor(pair) && sameNumber(pair) && differentSymbols(pair) && sameShading(pair)){
                        return pair;
                    }
                    if(sameColor(pair) && sameNumber(pair) && differentSymbols(pair) && differentShading(pair)){
                        return pair;
                    }
                    if(sameColor(pair) && sameNumber(pair) && sameSymbols(pair) && differentShading(pair)){
                        return pair;
                    }
                }
            }
        }
        return null;
    }

    private static boolean sameColor(PairObjects p){
        return p.elem1.color.equals(p.elem2.color) && p.elem2.color.equals(p.elem3.color);
    }

    private static boolean sameNumber(PairObjects p){
        return p.elem1.number == p.elem2.number && p.elem2.number == p.elem3.number;
    }

    private static boolean sameShading(PairObjects p){
        return p.elem1.shading.equals(p.elem2.shading) && p.elem2.shading.equals(p.elem3.shading);
    }

    private static boolean sameSymbols(PairObjects p){
        return p.elem1.symbol.equals(p.elem2.symbol) && p.elem2.symbol.equals(p.elem3.symbol);
    }
    private static boolean differentColor(PairObjects p){
        return !p.elem1.color.equals(p.elem2.color) &&
                !p.elem1.color.equals(p.elem3.color) &&
                !p.elem2.color.equals(p.elem3.color);
    }
    private static boolean differentShading(PairObjects p){
        return !p.elem1.shading.equals(p.elem2.shading) &&
                !p.elem1.shading.equals(p.elem3.shading) &&
                !p.elem2.shading.equals(p.elem3.shading);
    }
    private static boolean differentSymbols(PairObjects p){
        return !p.elem1.symbol.equals(p.elem2.symbol) &&
                !p.elem1.symbol.equals(p.elem3.symbol) &&
                !p.elem2.symbol.equals(p.elem3.symbol);
    }
    private static boolean differentNumber(PairObjects p){
        return p.elem1.number != p.elem2.number &&
                p.elem1.number != p.elem3.number &&
                p.elem2.number != p.elem3.number;
    }

    private static void clickElement(PairObjects p){
        WebElement board = driver.findElement(By.id("board"));
        List<WebElement> webObjects = board.findElements(By.tagName("td"));
        webObjects.get(p.elem1.id).click();
        webObjects.get(p.elem2.id).click();
        webObjects.get(p.elem3.id).click();
    }

    private static void getResults(){
        WebElement time = driver.findElement(By.id("duration"));
        WebElement score = driver.findElement(By.xpath("//*[@id=\"menu\"]/p[4]"));
        WebElement setsFound = driver.findElement(By.xpath("//*[@id=\"menu\"]/p[3]"));
        System.out.println("Time: " + time.getText() + " sec");
        System.out.println(score.getText());
        System.out.println(setsFound.getText());
    }












}
