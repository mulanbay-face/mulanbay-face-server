package cn.mulanbay.face.api.web.bean.request.picture;

import cn.mulanbay.business.enums.SearchType;
import cn.mulanbay.persistent.query.Parameter;
import cn.mulanbay.persistent.query.Query;
import cn.mulanbay.web.bean.request.PageSearch;

public class PictureDiscoverSearch extends PageSearch {

    @Query(fieldName = "title", op = Parameter.Operator.LIKE)
    private String name;

    @Query(fieldName = "searchType", op = Parameter.Operator.EQ)
    private SearchType searchType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SearchType getSearchType() {
        return searchType;
    }

    public void setSearchType(SearchType searchType) {
        this.searchType = searchType;
    }
}
