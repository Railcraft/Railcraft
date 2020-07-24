@echo OFF
set source=extras\art\textures\metals
set items=src\main\resources\assets\railcraft\textures\items
set blocks=src\main\resources\assets\railcraft\textures\blocks

echo Extracting %1 sprites...
echo.
echo ingot_%1.png
magick -extract 16x16+0+0 %source%\%1.png PNG32:%items%\ingot_%1.png
echo nugget_%1.png
magick -extract 16x16+16+0 %source%\%1.png PNG32:%items%\nugget_%1.png
echo plate_%1.png
magick -extract 16x16+32+0 %source%\%1.png PNG32:%items%\plate_%1.png
echo electrode_%1.png
magick -extract 16x16+48+0 %source%\%1.png PNG32:%items%\electrode_%1.png
echo metal_%1.png
magick -extract 16x16+0+16 %source%\%1.png PNG32:%blocks%\metal_%1.png
echo gear_%1.png
magick -extract 16x16+16+16 %source%\%1.png PNG32:%items%\gear_%1.png
echo bar_%1.png
magick -extract 16x16+48+16 %source%\%1.png PNG32:%items%\bar_%1.png

del %items%\ingot_iron.png 2> nul
del %items%\ingot_gold.png 2> nul
del %items%\nugget_iron.png 2> nul
del %items%\nugget_gold.png 2> nul
del %blocks%\metal_iron.png 2> nul
del %blocks%\metal_gold.png 2> nul