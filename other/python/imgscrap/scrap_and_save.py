import os
import requests
from selenium import webdriver
from selenium.webdriver.common.by import By
import time

# Kreiraj folder za slike ako ne postoji
if not os.path.exists('slike_proizvoda'):
    os.makedirs('slike_proizvoda')

options = webdriver.ChromeOptions()
driver = webdriver.Chrome(options=options)

# Koristimo session za efikasnije skidanje slika
session = requests.Session()

def download_image(url, folder, filename):
    try:
        response = session.get(url, stream=True)
        if response.status_code == 200:
            with open(os.path.join(folder, filename), 'wb') as f:
                for chunk in response.iter_content(1024):
                    f.write(chunk)
            return True
    except Exception:
        return False
    return False

def scrape_wolt(url):
    driver.get(url)
    time.sleep(5) 
    
    products = driver.find_elements(By.CSS_SELECTOR, '[data-test-id="horizontal-item-card"]')
    
    for product in products:
        try:
            title = product.find_element(By.CSS_SELECTOR, '[data-test-id="horizontal-item-card-header"]').text.replace("/", "_").replace(" ", "_")
            images = product.find_elements(By.CSS_SELECTOR, '[data-test-id="horizontal-item-card-image"]')
            
            for index, img in enumerate(images):
                img_url = img.get_attribute('src')
                
                if img_url:
                    # LOGIKA: Ukloni stare parametre i dodaj ?w=1920
                    # Ovo osigurava da uvijek skidamo najveću verziju
                    base_url = img_url.split('?')[0]
                    high_res_url = f"{base_url}?w=1920"
                    
                    filename = f"{title}_{index + 1}.jpg"
                    
                    print(f"Proizvod: {title} | Slika: {high_res_url}")
                    
                    if download_image(high_res_url, 'slike_proizvoda', filename):
                        print(f"   -> [SAČUVANO U VISOKOJ REZOLUCIJI] {filename}")
                    
        except Exception as e:
            print(f"Greška: {e}")
            
    print("\n--- Scraping završen! ---")
    driver.quit()

scrape_wolt("your-url")
