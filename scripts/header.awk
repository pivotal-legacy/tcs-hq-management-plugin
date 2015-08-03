BEGIN {header=0}
header == 0 && ($1 == "package" || $1 == "import" || $1 == "enum" || $1 == "final") {header=1; print_header()}
header == 1 && !(($1 == "/*" || $1 == "//") && $2 == "Copyright") {print}

function print_header() {
    print copyright
	print ""
}