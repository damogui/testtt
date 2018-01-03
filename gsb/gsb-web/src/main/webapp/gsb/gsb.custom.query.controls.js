
org.netsharp.controls.DictComboBoxQueryItem = org.netsharp.controls.ReferenceBoxQueryItem.Extends({
    get : function () {

        var propertyValue = $('#' + this.propertyName).combobox('getValue');
        if (System.isnull(propertyValue) ||propertyValue=="-1") {
            return null;
        }

        var foreignkey = $(this.uiElement).attr("foreignkey");
        var qp = new org.netsharp.core.QueryParameter();
        qp.ParameterName = "@" + foreignkey;
        qp.Value = propertyValue;
        qp.Filter = foreignkey + "='" + qp.Value + "'";

        return qp;
    },
	clear: function() {
		$('#' + this.propertyName).combobox('setValue','-1');
	}
});