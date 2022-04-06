package go_heavy

import (
	"fmt"
	"github.com/GoogleCloudPlatform/functions-framework-go/functions"
	"io"
	"net/http"
	"sort"
)

func init() {
	functions.HTTP("HelloWorld", goHeavy)
}

func goHeavy(w http.ResponseWriter, r *http.Request) {
	strs, err := io.ReadAll(r.Body)
	defer r.Body.Close()
	if err != nil {
		http.Error(w, err.Error(), 500)
		return
	}
	result := sortString(string(strs))
	fmt.Fprintln(w, result)
}

func sortString(input string) string {
	runeArray := []rune(input)
	sort.Sort(sortRuneString(runeArray))
	return string(runeArray)
}

type sortRuneString []rune

func (s sortRuneString) Swap(i, j int) {
	s[i], s[j] = s[j], s[i]
}

func (s sortRuneString) Less(i, j int) bool {
	return s[i] < s[j]
}

func (s sortRuneString) Len() int {
	return len(s)
}
