# Public transportation tap on-off hypothetical system

# How to run
1. Clone the repository:<br> `git clone https://github.com/JimmyyPengg/little-pay.git`
2. Go to the project:<br> `cd <path-to-the-project>/little-pay`
3. Build the project:<br> `mvn install`
4. Run it via mvn cli:<br> `mvn exec:java -Dexec.mainClass=com.little.pay.Main`
5. After that you should be able to see the result under `src/main/resources/output.csv`

Note: If you want to run the project with custom input csv and output csv, you can run the following cli.<br>
Also, **both input and output csv file path need to be provided, the first argument must be the input csv and the second argument must be the output csv**<br>
`mvn exec:java -Dexec.mainClass=com.little.pay.Main -Dexec.args="/Users/<username>/Desktop/input.csv /Users/<username>/Desktop/output.csv"`

# Assumptions
This code can only run correctly under the following assumptions:
 - There are only three stop id can be used in the input.csv: **Stop1**, **Stop2** and **Stop3**
 - All the tap records that belong to the same trip are in the same input.csv file
 - All buses are running under the same company
 - If there is no corresponding tap ON or OFF record, this trip will be considered as incomplete trip.
 - We are using _null_ for the missing information for the incomplete trip in the output csv