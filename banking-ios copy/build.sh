for i in "Allianz Dev" "Allianz UAT" "Allianz Preprod" "Allianz Prod"
do
	xcodebuild -scheme "$i" archive
done
