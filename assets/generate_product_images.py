from PIL import Image, ImageDraw, ImageFont
import os

# Create output directory
output_dir = "product_images"
os.makedirs(output_dir, exist_ok=True)

# List of products with name and description (for label)
products = [
    ("Green Tea Leaves", "Green Tea"),
    ("Arabica Coffee Beans", "Coffee Beans"),
    ("Porcelain Teacup Set", "Teacup"),
    ("Automatic Espresso Machine", "Espresso"),
    ("Herbal Chamomile Blend", "Chamomile"),
    ("Dark Roast Coffee Beans", "Dark Coffee"),
    ("Travel Mug - Insulated", "Mug"),
    ("Manual French Press", "French Press"),
    ("Peppermint Herbal Tea", "Peppermint"),
    ("Tea Infuser Spoon", "Infuser"),
]

# Colors for background diversity
colors = [
    (34, 139, 34),   # Forest Green
    (165, 42, 42),   # Brown
    (255, 222, 173), # Light Peach
    (101, 67, 33),   # Dark Coffee
    (255, 235, 188), # Light Yellow
    (139, 69, 19),   # SaddleBrown
    (176, 224, 230), # Powder Blue
    (255, 204, 0),   # Golden
    (144, 238, 144), # Light Green
    (192, 192, 192), # Silver
]

# Font setup
font_size = 30  # Increased font size
try:
    font = ImageFont.truetype("arial.ttf", font_size)
except:
    font = ImageFont.load_default()

print("Generating product images...")

for idx, (product_name, label) in enumerate(products):
    bg_color = colors[idx % len(colors)]

    # Create blank image
    img_size = (300, 300)  # Increased image size for better spacing
    image = Image.new('RGB', img_size, color=bg_color)
    draw = ImageDraw.Draw(image)

    # Estimate text width and height
    text_width = draw.textlength(label, font=font)
    text_height = font_size

    # Draw semi-transparent black rectangle behind text
    padding = 10
    rect_x0 = (img_size[0] - text_width) / 2 - padding
    rect_y0 = (img_size[1] - text_height) / 2 - padding
    rect_x1 = (img_size[0] + text_width) / 2 + padding
    rect_y1 = (img_size[1] + text_height) / 2 + padding

    overlay = Image.new('RGBA', img_size, (0, 0, 0, 0))
    overlay_draw = ImageDraw.Draw(overlay)
    overlay_draw.rounded_rectangle(
        [(rect_x0, rect_y0), (rect_x1, rect_y1)],
        radius=10,
        fill=(0, 0, 0, 150)  # Semi-transparent black
    )

    # Paste overlay on main image
    image = Image.alpha_composite(image.convert('RGBA'), overlay)
    draw = ImageDraw.Draw(image)

    # Center text inside the rectangle
    x = (img_size[0] - text_width) / 2
    y = (img_size[1] - text_height) / 2
    draw.text((x, y), label, fill=(255, 255, 255), font=font)

    # Save image
    filename = f"{label.replace(' ', '_').lower()}.png"
    image.save(os.path.join(output_dir, filename))

print(f"✅ Done! {len(products)} images saved to '{output_dir}'")
