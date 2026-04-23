from selenium import webdriver
from selenium.webdriver.common.by import By
import time

options = webdriver.ChromeOptions()
# options.add_argument('--headless') 
driver = webdriver.Chrome(options=options)

def scrape_images(url):
    driver.get(url)
    time.sleep(5) # Čekaj da se stranica učita (možeš koristiti i WebDriverWait)
    
    # Pronađi sve kartice proizvoda po data-test-id koji si poslao
    products = driver.find_elements(By.CSS_SELECTOR, '[data-test-id="horizontal-item-card"]')
    print(f"Pronađeno {len(products)} proizvoda.")
    
    for product in products:
        try:
            # Pronađi sliku unutar te konkretne kartice
            img_element = product.find_element(By.CSS_SELECTOR, '[data-test-id="horizontal-item-card-image"]')
            img_url = img_element.get_attribute('src')
            
            # Pronađi naziv proizvoda (da znaš čija je slika)
            title = product.find_element(By.CSS_SELECTOR, '[data-test-id="horizontal-item-card-header"]').text
            
            print(f"Proizvod: {title} | Slika: {img_url}")
            
        except Exception as e:
            print("Nije pronađena slika za jedan od proizvoda.")
            
    driver.quit()

scrape_images("your-url")    
